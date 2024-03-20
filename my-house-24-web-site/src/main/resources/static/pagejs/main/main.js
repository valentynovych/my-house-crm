
$(document).ready(function () {
    getMainPage();
});

function getMainPage() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "home/get",
        success: function (response) {
            console.log(response);
            showPage(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function showPage(response) {
    $("#image1").attr("src", '../uploads/'+response.image1);
    $("#image2").attr("src", "../uploads/"+response.image2);
    $("#image3").attr("src", "../uploads/"+response.image3);
    $("#title").text(response.title);
    $("#text").text(response.text);
    if(response.showLinks){
        $("#show-links").append(
            `<a href="">
                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/7/78/Google_Play_Store_badge_EN.svg/2560px-Google_Play_Store_badge_EN.svg.png"
                        style="max-width: 80%">
            </a>
            <a href="">
                <img src="https://upload.wikimedia.org/wikipedia/commons/thumb/3/3c/Download_on_the_App_Store_Badge.svg/2560px-Download_on_the_App_Store_Badge.svg.png"
                         style="max-width: 80%">
            </a>`
        );
    }
    $("#contacts").append(
        `<div>${response.contactsResponse.fullName}</div>
         <div>${response.contactsResponse.location}</div>
         <div>${response.contactsResponse.address}</div>
         <div>${response.contactsResponse.phoneNumber}</div>
         <div>${response.contactsResponse.email}</div>`
    );
    showMainBlocks(response);
}

function showMainBlocks(response) {
    for(let block of response.mainPageBlocks){
        $("#blocks").append(
            `<div class="col-md-4">
                <img style="max-width: 100%" src="${'../uploads/'+block.image}">
                <h4 class="mt-2">${block.title}</h4>
                <div class="mt-3">${block.description}</div>
            </div>`
        );
    }
}