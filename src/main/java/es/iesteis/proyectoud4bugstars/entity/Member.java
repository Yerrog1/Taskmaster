package es.iesteis.proyectoud4bugstars.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Entity
public class Member {
	@Id
	@Column(name = "id")
	private String id;

	@Column(name = "real_name", nullable = false)
	private String realName;

	@Column(name = "biography", length = 65530, columnDefinition = "text")
	private String biography;

	@Column(name = "email", nullable = false, unique = true)
	private String email;

	@Column(name = "email_code", unique = true)
	private String emailCode;

	@Column(name = "email_verified", nullable = false)
	private Boolean emailVerified;

	@Column(name = "password", nullable = false)
	private String encryptedPassword;

	@Column(name = "is_super_admin", nullable = false)
	private Boolean isSuperAdmin;

	@OneToMany(mappedBy = "member")
	private List<ProfileLink> profileLinks;

	@JsonIgnore
	@OneToMany(mappedBy = "creator")
	private Set<Task> tasksCreated;

	@JsonIgnore
	@OneToMany(mappedBy = "assignedMember")
	private Set<Task> tasksAssigned;

	@JsonIgnore
	@ManyToMany(mappedBy = "members")
	private Collection<Project> projects = new ArrayList<>();

	public Collection<Project> getProjects() {
		return projects;
	}

	public void setProjects(Collection<Project> projects) {
		this.projects = projects;
	}

	public Member() {
	}

	public String getId() {
		return id;
	}

	public Member setId(String id) {
		this.id = id;
		return this;
	}

	public String getRealName() {
		return realName;
	}

	public Member setRealName(String realName) {
		this.realName = realName;
		return this;
	}

	public String getBiography() {
		return biography;
	}

	public Member setBiography(String biography) {
		this.biography = biography;
		return this;
	}

	public String getEmail() {
		return email;
	}

	public Member setEmail(String email) {
		this.email = email;
		return this;
	}

	public String getEmailCode() {
		return emailCode;
	}

	public Member setEmailCode(String emailCode) {
		this.emailCode = emailCode;
		return this;
	}

	public Boolean getEmailVerified() {
		return emailVerified;
	}

	public Member setEmailVerified(Boolean emailVerified) {
		this.emailVerified = emailVerified;
		return this;
	}

	public String getEncryptedPassword() {
		return encryptedPassword;
	}

	public Member setEncryptedPassword(String encryptedPassword) {
		this.encryptedPassword = encryptedPassword;
		return this;
	}

	public Boolean getSuperAdmin() {
		return isSuperAdmin;
	}

	public Member setSuperAdmin(Boolean superAdmin) {
		isSuperAdmin = superAdmin;
		return this;
	}

	public Set<Task> getTasksCreated() {
		return tasksCreated;
	}

	public Member setTasksCreated(Set<Task> tasksCreated) {
		this.tasksCreated = tasksCreated;
		return this;
	}

	public Set<Task> getTasksAssigned() {
		return tasksAssigned;
	}

	public Member setTasksAssigned(Set<Task> tasksAssigned) {
		this.tasksAssigned = tasksAssigned;
		return this;
	}

	public List<ProfileLink> getProfileLinks() {
		return profileLinks;
	}

	public Member setProfileLinks(List<ProfileLink> profileLinks) {
		this.profileLinks = profileLinks;
		return this;
	}
}
