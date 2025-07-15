// JwtAuthenticationFilter.java
package com.project.ProjectSalon.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwt;
	private final UserDetailsService uds;

	public JwtAuthenticationFilter(JwtUtil jwt, UserDetailsService uds) {
		this.jwt = jwt; this.uds = uds;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest req,
									HttpServletResponse res,
									FilterChain chain)
			throws ServletException, IOException {

		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer ")) {
			String token    = header.substring(7);
			String username = jwt.getUsernameFromToken(token);

			if (username != null &&
					SecurityContextHolder.getContext().getAuthentication() == null) {

				UserDetails userDetails = uds.loadUserByUsername(username);

				if (jwt.validateToken(token, userDetails)) {

					/* build authorities: ROLE_ + plain‑role */
					String role = jwt.getRoleFromToken(token);      // "CUSTOMER"
					var auth = new UsernamePasswordAuthenticationToken(
							userDetails, null,
							List.of(new SimpleGrantedAuthority("ROLE_" + role))
					);
					auth.setDetails(new WebAuthenticationDetailsSource().buildDetails(req));
					SecurityContextHolder.getContext().setAuthentication(auth);
				}
			}
		}
		chain.doFilter(req, res);
	}
}
