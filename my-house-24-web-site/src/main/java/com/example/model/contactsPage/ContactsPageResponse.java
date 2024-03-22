package com.example.model.contactsPage;

public record ContactsPageResponse(
        String title,
        String text,
        String linkToSite,
        String fullName,
        String location,
        String address,
        String phoneNumber,
        String email,
        String mapCode
) {
}
