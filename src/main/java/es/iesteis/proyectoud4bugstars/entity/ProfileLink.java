package es.iesteis.proyectoud4bugstars.entity;

import jakarta.persistence.*;

@Entity
public class ProfileLink {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	private Member member;

	private String name;

	private String url;

	private Boolean isVerified;

	public ProfileLink() {
	}

	public Long getId() {
		return id;
	}

	public ProfileLink setId(Long id) {
		this.id = id;
		return this;
	}

	public String getName() {
		return name;
	}

	public ProfileLink setName(String name) {
		this.name = name;
		return this;
	}

	public String getUrl() {
		return url;
	}

	public ProfileLink setUrl(String url) {
		this.url = url;
		return this;
	}

	public Boolean getVerified() {
		return isVerified;
	}

	public ProfileLink setVerified(Boolean verified) {
		isVerified = verified;
		return this;
	}

	public Member getMember() {
		return member;
	}

	public ProfileLink setMember(Member member) {
		this.member = member;
		return this;
	}
}
