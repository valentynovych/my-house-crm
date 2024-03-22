
$(document).ready(function () {
    getContactsPage();
});

function getContactsPage() {
    blockBy('.content-wrapper');
    $.ajax({
        type: "GET",
        url: "contacts/get",
        success: function (response) {
            console.log(response);
            showPage(response);
            unblockBy('.content-wrapper');
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function showPage(response) {
    $("#title").text(response.title);
    $("#text").text(response.text);
    $("#contacts").append(
        `<div><i class="bi bi-person-circle"></i> ${response.fullName}</div>
         <div><i class="bi bi-geo-alt-fill"></i> ${response.location}</div>
         <div><i class="bi bi-compass"></i> ${response.address}</div>
         <div><i class="bi bi-telephone-fill"></i> ${response.phoneNumber}</div>
         <div><i class="bi bi-envelope"></i> ${response.email}</div>`
    );
}