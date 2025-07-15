package com.project.ProjectSalon.dto;


import com.project.ProjectSalon.entity.Users;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class RegistrationRequest {
   
    private String username;
    private String email;
    private String password;

	@Enumerated(EnumType.STRING)
	private Users.UserRole role;

	public String getUsername() {
		return username;
	}

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