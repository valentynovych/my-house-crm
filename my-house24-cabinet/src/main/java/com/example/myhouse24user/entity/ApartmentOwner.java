package com.example.myhouse24user.entity;

import jakarta.persistence.*;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "apartment_owners")
public class ApartmentOwner {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "owner_id",length = 10, nullable = false)
    private String ownerId;
    @Column(name = "first_name",length = 50, nullable = false)
    private String firstName;
    @Column(name = "last_name",length = 50, nullable = false)
    private String lastName;
    @Column(name = "middle_name",length = 50, nullable = false)
    private String middleName;
    @Column(name = "birth_date")
    private Instant birthDate;
    @Column(name = "creation_date", nullable = false)
    private Instant creationDate;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private OwnerStatus status;
    @Column(name = "about_owner", length = 300, nullable = false)
    private String aboutOwner;
    @Column(name = "phone_number",length = 13, nullable = false)
    private String phoneNumber;
    @Column(name = "viber_number",length = 13)
    private String viberNumber;
    @Column(name = "telegram_username",length = 50)
    private String telegramUsername;
    @Column(length = 100, nullable = false)
    private String email;
    @Column(length = 200, nullable = false)
    private String avatar;
    @Column(length = 72, nullable = false, unique = true)
    private String password;
    @Column(nullable = false)
    private boolean deleted;
    @Enumerated(EnumType.STRING)
    private Language language;
    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "owners_messages",
            joinColumns = { @JoinColumn(name = "owner_id") },
            inverseJoinColumns = { @JoinColumn(name = "message_id") }
    )
    private List<Message> messages = new ArrayList<>();
    @OneToMany(mappedBy = "owner", fetch = FetchType.EAGER)
    private List<Apartment> apartments = new ArrayList<>();
    @OneToOne(mappedBy = "apartmentOwner")
    private OwnerPasswordResetToken ownerPasswordResetToken;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getMiddleName() {
        return middleName;
    }

    public void setMiddleName(String middleName) {
        this.middleName = middleName;
    }

    public Instant getBirthDate() {
        return birthDate;
    }

    public void setBirthDate(Instant birthDate) {
        this.birthDate = birthDate;
    }

    public Instant getCreationDate() {
        return creationDate;
    }

    public void setCreationDate(Instant creationDate) {
        this.creationDate = creationDate;
    }

    public OwnerStatus getStatus() {
        return status;
    }

    public void setStatus(OwnerStatus status) {
        this.status = status;
    }

    public String getAboutOwner() {
        return aboutOwner;
    }

    public void setAboutOwner(String aboutOwner) {
        this.aboutOwner = aboutOwner;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getViberNumber() {
        return viberNumber;
    }

    public void setViberNumber(String viberNumber) {
        this.viberNumber = viberNumber;
    }

    public String getTelegramUsername() {
        return telegramUsername;
    }

    public void setTelegramUsername(String telegramUsername) {
        this.telegramUsername = telegramUsername;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public Language getLanguage() {
        return language;
    }

    public void setLanguage(Language language) {
        this.language = language;
    }

    public List<Message> getMessages() {
        return messages;
    }

    public void setMessages(List<Message> messages) {
        this.messages = messages;
    }

    public List<Apartment> getApartments() {
        return apartments;
    }

    public void setApartments(List<Apartment> apartments) {
        this.apartments = apartments;
    }

    public OwnerPasswordResetToken getOwnerPasswordResetToken() {
        return ownerPasswordResetToken;
    }

    public void setOwnerPasswordResetToken(OwnerPasswordResetToken ownerPasswordResetToken) {
        this.ownerPasswordResetToken = ownerPasswordResetToken;
    }
}
