let defaultMainPge;
let i;
let lastId = 0;
let deleteId;
let idsToDelete = [];
let descriptions = [];
const currentUrl = window.location.href;
const myArray = currentUrl.split("/");
var root = myArray[3];
$(document).ready(function () {
    initializeAutosize();
    getMainPage();
});

function initializeAutosize() {
    autosize($("#seoDescription"));
    autosize($("#seoKeywords"));
}

function getMainPage() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "home-page/get",
        success: function (response) {
            console.log(response);
            defaultMainPge = response;
            setFields(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function setFields(response) {
    $("#title").val(response.title);
    const convertedText = text.clipboard.convert(response.text)
    text.setContents(convertedText, 'silent');
    $("#showLinks").prop("checked", response.showLinks);
    setImages(response);
    drawBlocks(response);
    $("#seoTitle").val(response.seoTitle);
    $("#seoDescription").val(response.seoDescription);
    $("#seoKeywords").val(response.seoKeywords);
}
function setImages(response) {
    $("#img1").attr("src", "/"+root+"/uploads/"+response.image1);
    $("#img2").attr("src", "/"+root+"/uploads/"+response.image2);
    $("#img3").attr("src", "/"+root+"/uploads/"+response.image3);
}

function drawBlocks(response) {
    i = 0;
    for (const service of response.mainPageBlocks) {
        $("#blocks").append(
            `<div class="col-md-4 mb-4" id="${service.id}" name="${i}">
                <div class="col-md-11" style="position:relative;">
                    <h5 name="serviceName">${block + ' ' + (i + 1)}</h5>
                        <div>
                            <img  src="${service.image == null ? "https://highfield-school.co.uk/wp-content/uploads/2021/02/orionthemes-placeholder-image-600x400.png" : "/" + root + "/uploads/" + service.image}"
                              style="width: 100%; height: auto;"
                              id="${"image" + service.id}"
                              name="service-image">
                        <p class="my-2">${recommendedSize}</p>
                            <label class="input-group-text col-form-label" for="${"image-input" + service.id}">${imageLabel}</label>
                            <input type="file" accept=".jpeg, .jpg, .png" onchange="setImage(this)" class="form-control d-none"  id="${"image-input" + service.id}" name="servicePageBlocks[${i}]">
                        </div>
                        <button type="button" class="btn btn-icon btn-label-danger" onclick="openDeleteModal(${service.id})"  style="position: absolute; float:right; z-index: 1; top: 2%; right: -7%;">
                            <span class="ti ti-x"></span>
                        </button>
                    <div>
                        <label for="title" class="form-label mt-3">${title}</label>
                        <input type="text" value="${service.title}" class="form-control title" name="mainPageBlocks[${i}].title" placeholder="${title}"
                           maxlength="100">
                    </div>
                    <div>
                        <label for="description" class="form-label mt-3" >${description}</label>
                        <div id="${"description" + service.id}" name="mainPageBlocks[${i}].description"></div>
                    </div>
                </div>
            </div>`
        );
        initializeQuillAndSetText("description" + service.id, service.description);
        i++;
        if (i === response.mainPageBlocks.length) {
            lastId = service.id;
        }
    }
}

function initializeQuillAndSetText(descriptionId, description) {
    const fullEditor = new Quill("#" + descriptionId, {
        bounds: '#full-editor',
        placeholder: 'Type Something...',
        modules: {
            formula: true,
            toolbar: fullToolbar
        },
        theme: 'snow'
    });
    const convertedDescription = fullEditor.clipboard.convert(description);
    fullEditor.setContents(convertedDescription, 'silent');
    descriptions.push(fullEditor);
}

$("#add-button").on("click", function () {
    lastId++;
    let descriptionId = "description" + lastId;
    $("#blocks").append(
        `<div class="col-md-4 mb-4" id="${lastId}" name="${i}">
                <div class="col-md-11" style="position:relative;">
                    <h5 name="serviceName">${block + ' ' + (i + 1)}</h5>
                        <div>
                            <img  src="https://highfield-school.co.uk/wp-content/uploads/2021/02/orionthemes-placeholder-image-600x400.png"
                              style="width: 100%; height: auto;"
                              id="${"image" + i}"
                              name="service-image">
                        <p class="my-2">${recommendedSize}</p>
                            <label class="input-group-text col-form-label" for="${"image-input" + lastId}">${imageLabel}</label>
                            <input type="file" accept=".jpeg, .jpg, .png" onchange="setImage(this)" class="form-control d-none"  id="${"image-input" + lastId}" name="mainPageBlocks[${i}]">
                        </div>
                        <button type="button" class="btn btn-icon btn-label-danger" onclick="openDeleteModal(${lastId})"  style="position: absolute; float:right; z-index: 1; top: 2%; right: -7%;">
                            <span class="ti ti-x"></span>
                        </button>
                        <div>
                            <label for="title" class="form-label mt-3">${title}</label>
                            <input type="text" class="form-control title" name="mainPageBlocks[${i}].title" placeholder="${title}"
                           maxlength="100">
                        </div>
                        <div>
                            <label for="description" class="form-label mt-3">${description}</label>
                            <div id="${descriptionId}" name="mainPageBlocks[${i}].description"></div>
                        </div>
                </div>
        </div>`
    );
    i++;
    initializeQuill(descriptionId);
});

function initializeQuill(id) {
    const fullEditor = new Quill("#" + id, {
        bounds: '#full-editor',
        placeholder: 'Type Something...',
        modules: {
            formula: true,
            toolbar: fullToolbar
        },
        theme: 'snow'
    });
    descriptions.push(fullEditor);
}

function openDeleteModal(id) {
    if ($("#deleteModal").length === 0) {
        $("div.card-body").append(
            `<div class="modal fade" id="deleteModal" tabindex="-1" aria-labelledby="exampleModalLabel"
             aria-hidden="true">
            <div class="modal-dialog modal-dialog-centered">
                <div class="modal-content">
                    <div class="modal-header">
                        <button type="button" class="btn-close" data-bs-dismiss="modal"
                                aria-label="Close"></button>
                    </div>
                    <div class="modal-body">
                        <h4>${deleteModalText}</h4>
                    </div>
                    <div class="modal-footer">
                        <button type="button" class="btn btn-label-secondary close-modal" data-bs-dismiss="modal">
                        ${modalCloseButton}
                        </button>
                        <button type="button" class="btn btn-danger" id="delete-button" onclick="deleteBlock()">
                            ${modalDeleteButton}
                        </button>
                    </div>
                </div>
            </div>
        </div>`
        )
    }
    $('#deleteModal').modal('show');
    deleteId = id;
}

function deleteBlock() {
    idsToDelete.push(deleteId);
    let index = $("#" + deleteId).attr("name");
    $("#" + deleteId).remove();
    let ind = 1;
    $('h5[name="serviceName"]').each(function () {
        $(this).text(block + ' ' + ind);
        i = ind;
        ind++;
    });
    descriptions.splice(index, 1);
    $('#deleteModal').modal('hide');
}

$("#save-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    let formData = collectData();
    for (var pair of formData.entries()) {
        console.log(pair[0] + ', ' + pair[1]);
    }
    sendData(formData);
});

function sendData(formData) {
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            descriptions.length = 0;
            idsToDelete.length = 0;
            lastId = 0;
            $("#blocks").empty();
            getMainPage();
            toastr.success(successMessage);
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}

function collectData() {
    let formData = new FormData();
    formData.append("title", $("#title").val());
    formData.append("text", text.root.innerHTML);
    formData.append("showLinks", $("#showLinks").is(':checked'));
    for(let j = 1; j < 4; j++){
        let image = $("#image"+j+"-input").prop("files")[0];
        if(image === undefined) {
            formData.append("image"+j, new File([""], "filename"));
        } else {
            formData.append("image"+j, image);
        }
    }
    collectBlocks(formData);
    appendIdsToDeleteToFormData(formData);
    appendSeoToFormData(formData);
    return formData;
}

function collectBlocks(formData) {
    let ind = 0;
    $(".title").each(function () {
        let currentId = $(this).parent().parent().parent().attr("id");
        formData.append("mainPageBlocks[" + ind + "].id", currentId);
        let title = $(this).val();
        formData.append("mainPageBlocks[" + ind + "].title", title);
        $(this).attr("name", "mainPageBlocks[" + ind + "].title");
        let description = descriptions[ind].root.innerHTML;
        formData.append("mainPageBlocks[" + ind + "].description", description);
        $("#description" + currentId).attr("name", "mainPageBlocks[" + ind + "].description")
        let image = $('#image-input' + currentId).prop('files')[0];
        if (image === undefined) {
            formData.append("mainPageBlocks[" + ind + "].image", new File([""], "filename"));
            $('#image-input' + currentId).attr("name", "mainPageBlocks[" + ind + "]");
        } else {
            formData.append("mainPageBlocks[" + ind + "].image", image);
        }
        ind++;
    });
}

function appendIdsToDeleteToFormData(formData) {
    console.log(idsToDelete);
    for (let j = 0; j < idsToDelete.length; j++) {
        formData.append("idsToDelete[]", idsToDelete[j]);
    }
}

function appendSeoToFormData(formData) {
    formData.append("seoRequest.seoTitle", $("#seoTitle").val());
    formData.append("seoRequest.seoDescription", $("#seoDescription").val());
    formData.append("seoRequest.seoKeywords", $("#seoKeywords").val());
}

$("#image1-input").on("change", function () {
    setImage($(this));
});
$("#image2-input").on("change", function () {
    setImage($(this));
});
$("#image3-input").on("change", function () {
    setImage($(this));
});

function setImage(input) {
    var files = $(input).prop('files');
    if (validateFile(files[0].name) != false) {
        $(input).siblings("img").attr("src", window.URL.createObjectURL(files[0]));
    } else {
        $(input).parent().append(
            '<p class="text-danger mb-0">' + fileExtension + '</p>'
        );
        $(input).val('');
    }
}

function validateFile(value) {
    var ext = value.substring(value.lastIndexOf('.') + 1).toLowerCase();
    if ($.inArray(ext, ['png', 'jpg', 'jpeg']) == -1 && value != "") {
        return false;
    } else {
        return true;
    }
}

$("#cancel-button").on("click", function () {
    descriptions.length = 0;
    idsToDelete.length = 0;
    lastId = 0;
    $("#blocks").empty();
    setFields(defaultMainPge);
});

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
const text = new Quill('#text', {
    bounds: '#full-editor',
    placeholder: 'Type Something...',
    modules: {
        formula: true,
        toolbar: fullToolbar
    },
    theme: 'snow'
});
