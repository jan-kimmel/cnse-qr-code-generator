package de.hskl.cnseqrcode.api;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class FirebaseAuthFilter extends OncePerRequestFilter {
    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) 
            throws ServletException, IOException {
        
        String path = request.getRequestURI();
        System.out.println("=== Request: " + request.getMethod() + " " + path);
        
        if (request.getMethod().equalsIgnoreCase("OPTIONS")) {
            System.out.println("OPTIONS request - durchlassen");
            filterChain.doFilter(request, response);
            return;
        }
        
        String header = request.getHeader("Authorization");
        System.out.println("Authorization Header: " + (header != null ? "vorhanden" : "fehlt"));
        
        if (header == null || !header.startsWith("Bearer ")) {
            System.out.println("Kein Bearer Token - Request ohne Auth durchlassen");
            filterChain.doFilter(request, response);
            return;
        }
        
        try {
            String token = header.substring(7);
            System.out.println("Token (erste 20 Zeichen): " + token.substring(0, Math.min(20, token.length())));
            
            FirebaseToken decoded = FirebaseAuth.getInstance().verifyIdToken(token);
            String userId = decoded.getUid();
            
            System.out.println("Token verifiziert! User ID: " + userId);
            request.setAttribute("userId", userId);
            filterChain.doFilter(request, response);
            
        } catch (Exception e) {
            System.err.println("Firebase Auth Error: " + e.getMessage());
            e.printStackTrace();
            
            response.setStatus(HttpStatus.UNAUTHORIZED.value());
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            response.setHeader("Access-Control-Allow-Origin", request.getHeader("Origin"));
            response.setHeader("Access-Control-Allow-Headers", "Authorization, Content-Type");
            response.setHeader("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            response.setHeader("Access-Control-Allow-Credentials", "true");
            
            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"" + e.getMessage() + "\"}");
            return;
        }
    }
}
