
const currentUrl = window.location.href;
const myArray = currentUrl.split("/");
let root = myArray[3];
$(document).ready(function () {
    getProfile();
});

function getProfile() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "profile/get",
        success: function (response) {
            setFields(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function setFields(response) {
    const profileFields = new Map(Object.entries((response)));
    profileFields.forEach((value, key) => {
        $("#"+key).text(value);
    });
    $("#fullName").text(response.lastName+" "+response.firstName+" "+response.middleName);
    let avatarImage = response.avatar? '/'+root+ '/uploads/'+response.avatar: 'https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg';
    $("#ownerAvatar").attr("src",avatarImage);
    for(let apartment of response.apartmentResponses){
        let number = "";
        for (let j = 0; j < 10 - apartment.personalAccount.toString().length; j++) {
            number += "0";
        }
        number += apartment.personalAccount;
        let accountNumber = number.substring(0, 5) + "-" + number.substring(5, 10);
        $("#apartments").append(
            `<div class="px-2" style="border-style: solid; border-width: thin; border-color: #dbdade">
                    <div class="row g-4 mt-0">
                        <p class="mt-3 mb-0">${apartment.houseName + ". " + apartment.houseAddress + ", " + ap + " " + apartment.apartmentNumber}</p>
                            <div class="image-wrapper">
                                <figure style=" grid-area: first">
                                    <img class="img-thumbnail rounded"
                                     src="${apartment.image1 ? '/' + root + '/uploads/' + apartment.image1 : 'https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg'}"/>
                                 </figure>
                                <figure style=" grid-area: second">
                                    <img class="img-thumbnail rounded"
                                     src="${apartment.image2 ? '/' + root + '/uploads/' + apartment.image2 : 'https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg'}"/>
                                </figure>
                                <figure style=" grid-area: third">
                                    <img class="img-thumbnail rounded"
                                     src="${apartment.image3 ? '/' + root + '/uploads/' + apartment.image3 : 'https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg'}"/>
                                 </figure>
                                <figure style=" grid-area: four">
                                    <img class="img-thumbnail rounded"
                                     src="${apartment.image4 ? '/' + root + '/uploads/' + apartment.image4 : 'https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg'}"/>
                                 </figure>
                                <figure style=" grid-area: five">
                                    <img class="img-thumbnail rounded"
                                     src="${apartment.image5 ? '/' + root + '/uploads/' + apartment.image5 : 'https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg'}"/>
                                 </figure>
                            </div>
                    </div>
                    <table class="table table-bordered my-3">
                        <tbody class="table-border-bottom-0">
                        <tr>
                            <td colspan="2">${description}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${houseName}</td>
                            <td>${apartment.houseName}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${address}</td>
                            <td>${apartment.houseAddress}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${apartmentNumber}</td>
                            <td>${apartment.apartmentNumber}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${area}</td>
                            <td>${apartment.area}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${floor}</td>
                            <td>${apartment.floor}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${section}</td>
                            <td>${apartment.section}</td>
                        </tr>
                        <tr>
                            <td class="fw-bold w-25">${personalAccount}</td>
                            <td>${accountNumber}</td>
                        </tr>
                        </tbody>
                    </table>
                </div>`);
    }
}


