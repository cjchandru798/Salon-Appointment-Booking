package com.project.ProjectSalon.dto;

import com.project.ProjectSalon.entity.Users;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.Data;

@Data
public class LoginRequest {
    
    private String email;
    private String password;

	@Enumerated(EnumType.STRING)
	private Users.UserRole role;

	public String getEmail() {
		return email;
	}

	public String getPassword() {
		return password;
	}

	public Users.UserRole getRole() {
		return role;
	}
}