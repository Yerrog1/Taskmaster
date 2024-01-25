package es.iesteis.proyectoud4bugstars.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.Constraint;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.id.GUIDGenerator;

import java.util.Collection;
import java.util.UUID;

@Entity
@Table(indexes = @Index(name = "project_id", columnList = "owner_id, project_id", unique = true))
public class Project {
	@Id
	@GeneratedValue
	@UuidGenerator
	private String id;

	@ManyToOne
	@JoinColumn(name = "owner_id")
	private Member owner;

	@Column(name = "project_id")
	private String projectId;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	private boolean isPublic;

	@Column(name = "website")
	private String website;
	@JsonIgnore
	@ManyToMany
	private Collection<Member> members;
	@JsonIgnore
	@OneToMany(mappedBy = "project")
	private Collection<Task> tasks;

	public Project() {
	}

	public String getId() {
		return id;
	}

	public Project setId(String id) {
		this.id = id;
		return this;
	}

	public Member getOwner() {
		return owner;
	}

	public Project setOwner(Member owner) {
		this.owner = owner;
		return this;
	}

	public String getProjectId() {
		return projectId;
	}

	public Project setProjectId(String projectId) {
		this.projectId = projectId;
		return this;
	}

	public String getName() {
		return name;
	}

	public Project setName(String name) {
		this.name = name;
		return this;
	}

	public String getDescription() {
		return description;
	}

	public Project setDescription(String description) {
		this.description = description;
		return this;
	}

	public String getWebsite() {
		return website;
	}

	public Project setWebsite(String website) {
		this.website = website;
		return this;
	}

	public Collection<Member> getMembers() {
		return members;
	}

	public Project setMembers(Collection<Member> members) {
		this.members = members;
		return this;
	}

	public boolean isPublic() {
		return isPublic;
	}

	public Project setPublic(boolean aPublic) {
		isPublic = aPublic;
		return this;
	}
}
