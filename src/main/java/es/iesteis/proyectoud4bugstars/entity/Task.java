package es.iesteis.proyectoud4bugstars.entity;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(indexes = @Index(name = "task_id", columnList = "project_id, task_number", unique = true))
public class Task {
	@Id
	@UuidGenerator(style = UuidGenerator.Style.RANDOM)
	@GeneratedValue(strategy = GenerationType.UUID)
	private String id;

	@ManyToOne
	@JoinColumn(updatable = false, name = "project_id")
	private Project project;

	@Column(name = "task_number", updatable = false)
	private int taskNumber;

	@Column(name = "Status")
	private TaskStatus taskStatus;

	@Column(name = "name")
	private String name;

	@Column(name = "description")
	private String description;

	@Column(name = "created_at", updatable = false)
	private Instant createdAt = Instant.now();

	@Column(name = "last_updated_at")
	private Instant lastUpdatedAt = Instant.now();

	@ManyToOne
	@JoinColumn(name = "creator_id")
	private Member creator;

	@ManyToOne
	@JoinColumn(name = "assigned_to")
	private Member assignedMember;

	@PreUpdate
	public void preUpdate() {
		this.lastUpdatedAt = Instant.now();
	}

	public String getId() {
		return id;
	}

	public Task setId(String id) {
		this.id = id;
		return this;
	}

	public Project getProject() {
		return project;
	}

	public Task setProject(Project project) {
		this.project = project;
		return this;
	}

	public int getTaskNumber() {
		return taskNumber;
	}

	public Task setTaskNumber(int taskNumber) {
		this.taskNumber = taskNumber;
		return this;
	}

	public String getName() {
		return name;
	}

	public Task setName(String name) {
		this.name = name;
		return this;
	}


	public String getDescription() {
		return description;
	}

	public Task setDescription(String description) {
		this.description = description;
		return this;
	}

	public Member getAssignedMember() {
		return assignedMember;
	}

	public Task setAssignedMember(Member assignedMember) {
		this.assignedMember = assignedMember;
		return this;
	}

	public Member getCreator() {
		return creator;
	}

	public Task setCreator(Member creator) {
		this.creator = creator;
		return this;
	}

	public TaskStatus getTaskStatus() {
		return taskStatus;
	}

	public Task setTaskStatus(TaskStatus taskStatus) {
		this.taskStatus = taskStatus;
		return this;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public Task setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
		return this;
	}

	public Instant getLastUpdatedAt() {
		return lastUpdatedAt;
	}

	public Task setLastUpdatedAt(Instant lastUpdatedAt) {
		this.lastUpdatedAt = lastUpdatedAt;
		return this;
	}
}
