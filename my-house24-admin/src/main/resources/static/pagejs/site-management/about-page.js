let defaultAboutPage;
let additionalGalleryIdsToDelete = [];
let galleryIdsToDelete = [];
let documentIdsToDelete = []
let galleryUploadedFiles = []
let additionalGalleryUploadedFiles = []
let documentUploadedFiles = []
let galleryFileIndex = 0;
let additionalGalleryFileIndex = 0;
let documentFileIndex = 0;
let galleryShift = 0;
let additionalGalleryShift = 0;
let documentShift = 0;
$(document).ready(function () {
    initializeAutosize();
    getAboutPage();
});

function initializeAutosize() {
    autosize($("#seoDescription"));
    autosize($("#seoKeywords"));
}
function getAboutPage() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "about-page/get",
        success: function (response) {
            console.log(response);
            defaultAboutPage = response;
            setFields(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}
function setFields(response) {
    const responseMap = new Map(Object.entries((response)));
    responseMap.forEach((value, key) => {
        if(key.localeCompare("directorImage") !== 0) {
            $("#" + key).val(value);
        }
        $("#director-img").attr("src", "/"+root+"/uploads/"+response.directorImage);
    });
    const convertedAboutText = aboutText.clipboard.convert(response.aboutText)
    aboutText.setContents(convertedAboutText, 'silent');
    if(response.additionalText !== null) {
        const convertedAdditionalText = additionalText.clipboard.convert(response.additionalText);
        additionalText.setContents(convertedAdditionalText, 'silent');
    }
    setGallery(response.gallery, "gallery", "deleteGalleryImage");
    setGallery(response.additionalGallery, "additionalGallery", "deleteAdditionalGalleryImage");
    setDocuments(response.documents);
}
function setGallery(gallery, galleryId, method) {
    for(let image of gallery){
        $("#"+galleryId).append(
            `<div class="col-md-3" style="position: relative;" id="${image.id}">
                    <img src="${"/"+root+"/uploads/"+image.image}"
                         style="height: 100%; max-width: 100%">
                         <button type="button" class="btn rounded-pill btn-icon btn-label-danger" onclick="${method+'(this)'}"  style="position: absolute; float:right; z-index: 1; top: -6%; right: -1%; height: 25px; width: 25px;">
                            <span class="ti ti-x"></span>
                        </button>
                </div>`
        );
    }
}
function setDocuments(documents) {
    for(let document of documents){
        let index = document.name.indexOf("_");
        let documentName = document.name.slice(index+1);
        $("#documents").append(
            `<div class="col-md-2" style="position: relative;" id="${document.id}">
                    <img src="https://freeiconshop.com/wp-content/uploads/edd/documents-outline.png"
                         style="width: 100%; height: auto;"
                         class="d-block h-auto">
                         <button type="button" class="btn rounded-pill btn-icon btn-label-danger" onclick="deleteDocument(this)"  style="position: absolute; float:right; z-index: 1; top: -6%; right: -1%; height: 25px; width: 25px;">
                            <span class="ti ti-x"></span>
                        </button>
                        <p class="mt-1 text-center">${documentName}</p>
                </div>`
        );
    }
}
$("#gallery-input").on("change", function () {
    let files = $(this).prop("files");
    for(let file of files){
        if(validateFile(file.name)) {
            galleryUploadedFiles.push(file);
            $("#gallery").append(
                `<div class="col-md-3" style="position: relative;">
                    <img src="${window.URL.createObjectURL(file)}"
                         style="height: 100%; max-width: 100%"
                          id="${galleryFileIndex}">
                         <button type="button" class="btn rounded-pill btn-icon btn-label-danger" onclick="deleteGalleryImage(this)"  style="position: absolute; float:right; z-index: 1; top: -6%; right: -1%; height: 25px; width: 25px;">
                            <span class="ti ti-x"></span>
                        </button>
                </div>`
            );
            galleryFileIndex++;
        }
    }
});

$("#additional-gallery-input").on("change", function () {
    let files = $(this).prop("files");
    for(let file of files){
        if(validateFile(file.name)) {
            additionalGalleryUploadedFiles.push(file);
            $("#additionalGallery").append(
                `<div class="col-md-3" style="position: relative;">
                    <img src="${window.URL.createObjectURL(file)}"
                         style="height: 100%; max-width: 100%"
                          id="${additionalGalleryFileIndex}">
                         <button type="button" class="btn rounded-pill btn-icon btn-label-danger" onclick="deleteAdditionalGalleryImage(this)"  style="position: absolute; float:right; z-index: 1; top: -6%; right: -1%; height: 25px; width: 25px;">
                            <span class="ti ti-x"></span>
                        </button>
                </div>`
            );
            additionalGalleryFileIndex++;
        }
    }
});
$("#document-input").on("change", function () {
    let files = $(this).prop("files");
    for(let file of files){
        if(validateDocumentFile(file.name)) {
            documentUploadedFiles.push(file);
            $("#documents").append(
                `<div class="col-md-2" style="position: relative;">
                    <img src="https://freeiconshop.com/wp-content/uploads/edd/documents-outline.png"
                         style="width: 100%; height: auto;"
                         class="d-block h-auto"
                          id="${documentFileIndex}">
                         <button type="button" class="btn rounded-pill btn-icon btn-label-danger" onclick="deleteDocument(this)"  style="position: absolute; float:right; z-index: 1; top: -6%; right: -1%; height: 25px; width: 25px;">
                            <span class="ti ti-x"></span>
                        </button>
                        <p class="mt-1 text-center">${file.name}</p>
                </div>`
            );
            documentFileIndex++;
        }
    }
});
function deleteGalleryImage(image) {
    if($(image).siblings("img").attr("id") !== undefined){
        let ind = $(image).siblings("img").attr("id");
        let indShft = ind-galleryShift;
        galleryUploadedFiles.splice(indShft, 1);
        galleryShift++;
    } else {
        galleryIdsToDelete.push($(image).parent().attr("id"));
    }
    $(image).parent().remove();
}

function deleteAdditionalGalleryImage(image) {
    if($(image).siblings("img").attr("id") !== undefined){
        let ind = $(image).siblings("img").attr("id");
        let indShft = ind-additionalGalleryShift;
        additionalGalleryUploadedFiles.splice(indShft, 1);
        additionalGalleryShift++;
    } else {
        additionalGalleryIdsToDelete.push($(image).parent().attr("id"));
    }
    $(image).parent().remove();
}
function deleteDocument(document) {
    if($(document).siblings("img").attr("id") !== undefined){
        let ind = $(document).siblings("img").attr("id");
        let indShft = ind-documentShift;
        documentUploadedFiles.splice(indShft, 1);
        documentShift++;
    } else {
        documentIdsToDelete.push($(document).parent().attr("id"));
    }
    $(document).parent().remove();
}
$("#save-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    let formData = collectData();
    sendData(formData);
});

function collectData() {
    let formData = new FormData();
    $("#form").find('input:text, textarea').each(function (){
        formData.append($(this).attr("id"), $(this).val());
    });
    formData.append("aboutText", aboutText.root.innerHTML);
    formData.append("additionalText", additionalText.root.innerHTML);
    for(let id of galleryIdsToDelete){
        formData.append("galleryIdsToDelete[]", id);
    }
    for(let id of additionalGalleryIdsToDelete){
        formData.append("additionalGalleryIdsToDelete[]", id);
    }
    for(let id of documentIdsToDelete){
        formData.append("documentIdsToDelete[]", id);
    }
    for(let newImage of galleryUploadedFiles){
        formData.append("newImages[]", newImage);
    }
    for(let newImage of additionalGalleryUploadedFiles){
        formData.append("additionalNewImages[]", newImage);
    }
    for(let newDocument of documentUploadedFiles){
        formData.append("newDocuments[]", newDocument);
    }
    let directorImage = $("#directorImage").prop("files")[0];
    if(directorImage === undefined) {
        formData.append("directorImage", new File([""], "filename"));
    } else {
        formData.append("directorImage", directorImage);
    }

    formData.append("seoRequest.seoTitle", $("#seoTitle").val());
    formData.append("seoRequest.seoDescription", $("#seoDescription").val());
    formData.append("seoRequest.seoKeywords", $("#seoKeywords").val());
    return formData;
}

function sendData(formData) {
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            galleryIdsToDelete.length = 0;
            additionalGalleryIdsToDelete.length = 0;
            documentIdsToDelete = 0;
            galleryUploadedFiles.length = 0;
            additionalGalleryUploadedFiles.length = 0;
            documentUploadedFiles = 0;
            galleryShift = 0;
            additionalGalleryShift = 0;
            documentShift = 0;
            galleryFileIndex = 0;
            additionalGalleryFileIndex = 0;
            documentFileIndex = 0;
            $("#gallery").empty();
            $("#additionalGallery").empty();
            $("#documents").empty();
            getAboutPage();
            toastr.success(successMessage);
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}

$("#directorImage").on("change", function () {
    var myFile = $(this).prop('files');
    if(validateFile(myFile[0].name) != false){
        $(this).siblings("img").attr("src", window.URL.createObjectURL(myFile[0]));
    } else {
        $(this).parent().append(
            '<p class="text-danger mb-0">' + fileExtension + '</p>'
        );
        $(this).val('');
    }
});

function validateFile(value){
    var ext = value.substring(value.lastIndexOf('.') + 1).toLowerCase();
    if($.inArray(ext, ['png','jpg','jpeg']) == -1 && value != "") {
        return false;
    } else {
        return true;
    }
}

function validateDocumentFile(fileName) {
    var ext = fileName.substring(fileName.lastIndexOf('.') + 1).toLowerCase();
    if($.inArray(ext, ['doc', 'pptx', 'ppsx', 'docx', 'txt', 'pdf']) == -1 && fileName != "") {
        return false;
    } else {
        return true;
    }
}

const fullToolbar = [
    [
        {
            font: []
        },
        {
            size: []
        }
    ],
    ['bold', 'italic', 'underline', 'strike'],
    [
        {
            indent: '-1'
        },
        {
            indent: '+1'
        }
    ]
];
const aboutText = new Quill('#aboutText', {
    bounds: '#full-editor',
    placeholder: 'Type Something...',
    modules: {
        formula: true,
        toolbar: fullToolbar
    },
    theme: 'snow'
});

const additionalText = new Quill('#additionalText', {
    bounds: '#full-editor',
    placeholder: 'Type Something...',
    modules: {
        formula: true,
        toolbar: fullToolbar
    },
    theme: 'snow'
});
