
$(document).ready(function () {
    getAboutPage();
});

function getAboutPage() {
    blockBy('.content-wrapper');
    $.ajax({
        type: "GET",
        url: "about-us/get",
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
    $("#aboutText").html(response.aboutText);
    showGallery(response);
    $("#directorImage").append(
        `<div>
            <img style="max-width: 100%" src="${"uploads/"+response.directorImage}">
        </div>`
    );
    $("#additionalTitle").text(response.additionalTitle);
    if(response.additionalText !== null) {
        $("#additional").append(
            `<div class="mt-4 mb-5"> 
                ${response.additionalText}
            </div>`
        );
    }
    showAdditionalGallery(response);
    showDocuments(response);
}

function showGallery(response) {
    if(response.gallery.length !== 0) {
        $("#galleryDiv").append(
            `<div class="row row-cols-2 row-cols-lg-5 g-2 g-lg-3 mt-4" id="gallery"></div>`
        );
        for (let galleryImage of response.gallery) {
            $("#gallery").append(
                `<div class="col">
                <img style="max-width: 100%" src="${"uploads/" + galleryImage.image}">
            </div>`
            )
        }
    }
}

function showAdditionalGallery(response) {
    if(response.additionalGallery.length !== 0) {
        $("#additional").append(
            `<div class="row row-cols-2 row-cols-lg-5 g-2 g-lg-3 mt-4" id="additionalGallery"></div>`
        );
        for (let galleryImage of response.additionalGallery) {
            $("#additionalGallery").append(
                `<div class="col">
                    <img style="max-width: 100%" src="${"uploads/" + galleryImage.image}">
                </div>`
            )
        }
    }
}

function showDocuments(response) {
    if(response.documents.length !== 0) {
        $("#documentsDiv").append(
            `<div class="row row-cols-12  g-2 mt-4" id="documents"></div>`
        );
        for (let document of response.documents) {
            let firstIndex = document.name.indexOf("_")+1;
            let lastIndex = document.name.lastIndexOf(".")
            let name = document.name.substring(firstIndex, lastIndex);
            $("#documents").append(
                `<div class="col-2">
                    <img  class="ms-4"
                    style="max-width: 50%" src="https://freeiconshop.com/wp-content/uploads/edd/documents-outline.png">
                    <p class="mx-2">${name}</p>
                    <div>
                    <a href="about-us/download/${document.name}" type="button" 
                    class="btn btn-outline-info ms-1">
                    <i class="bi bi-download"></i> ${download}</a>
                    </div>
                </div>`
            )
        }
    }
}
