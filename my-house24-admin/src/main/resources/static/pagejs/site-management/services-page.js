let defaultServicesPage;
let deleteId;
let idsToDelete = [];
let descriptions = [];
let i;
let lastId;
$(document).ready(function () {
    initializeAutosize();
    getServicesPage();
});
function initializeAutosize() {
    autosize($("#seoDescription"));
    autosize($("#seoKeywords"));
}
function getServicesPage(){
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "service-page/get",
        success: function (response) {
            console.log(response);
            defaultServicesPage = response;
            drawServicesPage(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}
function drawServicesPage(response){
    const currentUrl = window.location.href;
    const myArray = currentUrl.split("/");
    let root = myArray[3];
    i = 0;
    for (const service of response.servicePageBlocks) {
        $("#services").append(
            `<div class="col-md-4 mb-4" id="${service.id}" name="${i}">
                <div class="col-md-11" style="position:relative;">
                    <h5 name="serviceName">${serviceNumber+' '+(i+1)}</h5>
                        <div>
                            <img  src="${service.image == null? "https://highfield-school.co.uk/wp-content/uploads/2021/02/orionthemes-placeholder-image-600x400.png": "/"+root+"/uploads/"+service.image}"
                              style="height: 100%; width: 100%"
                              id="${"image"+service.id}"
                              name="service-image">
                        <p class="my-2">${recommendedSize}</p>
                            <label class="input-group-text col-form-label" for="${"image-input" + service.id}">${imageLabel}</label>
                            <input type="file" accept=".jpeg, .jpg, .png" onchange="setImage(this)" class="form-control d-none"  id="${"image-input" + service.id}" name="servicePageBlocks[${i}]">
                        </div>
                        ${i != 0? getButton(service.id): ''}
                    <div>
                        <label for="title" class="form-label mt-3">${serviceName}</label>
                        <input type="text" value="${service.title}" class="form-control title" id="title" name="servicePageBlocks[${i}].title" placeholder="${serviceName}"
                           maxlength="100">
                    </div>
                    <div>
                        <label for="description" class="form-label mt-3" >${serviceDescription}</label>
                        <div id="${"description"+service.id}" name="servicePageBlocks[${i}].description"></div>
                    </div>
                </div>
            </div>`
        );
        initializeQuillAndSetText("description"+service.id, service.description);
        i++;
        if(i === response.servicePageBlocks.length){
            lastId = service.id;
        }
    }
    setSeoFields(response);
}
function initializeQuillAndSetText(descriptionId, description) {
    const fullEditor = new Quill("#"+descriptionId, {
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
function getButton(id){
    return '<button type="button" class="btn btn-icon btn-label-danger" onclick="openDeleteModal('+id+')"  style="position: absolute; float:right; z-index: 1; top: 4%; right: -5%;">\n              ' +
        '<span class="ti ti-x"></span>'+
        '      </button>';
}
function setSeoFields(response){
    $("#seoTitle").val(response.seoTitle);
    $("#seoDescription").val(response.seoDescription);
    $("#seoKeywords").val(response.seoKeywords);
}
function openDeleteModal(id){
    if($("#deleteModal").length === 0) {
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
                        <button type="button" class="btn btn-danger" id="delete-button" onclick="deleteService()">
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
function deleteService() {
    idsToDelete.push(deleteId);
    let index = $("#"+deleteId).attr("name");
    $("#"+deleteId).remove();
    let ind = 1;
    $('h5[name="serviceName"]').each(function () {
        $(this).text(serviceNumber+' '+ind);
        i = ind;
        ind++;
    });
    descriptions.splice(index, 1);
    $('#deleteModal').modal('hide');
}
$("#add-button").on("click",function () {
    lastId++;
    let descriptionId = "description"+lastId;
    $("#services").append(
        `<div class="col-md-4 mb-4" id="${lastId}" name="${i}">
                <div class="col-md-11" style="position:relative;">
                    <h5 name="serviceName">${serviceNumber+' '+(i+1)}</h5>
                        <div>
                            <img  src="https://highfield-school.co.uk/wp-content/uploads/2021/02/orionthemes-placeholder-image-600x400.png"
                              style="height: 100%; width: 100%"
                              id="${"image"+i}"
                              name="service-image">
                        <p class="my-2">${recommendedSize}</p>
                            <label class="input-group-text col-form-label" for="${"image-input" + lastId}">${imageLabel}</label>
                            <input type="file" accept=".jpeg, .jpg, .png" onchange="setImage(this)" class="form-control d-none"  id="${"image-input" + lastId}" name="servicePageBlocks[${i}]">
                        </div>
                        <button type="button" class="btn btn-icon btn-label-danger" onclick="openDeleteModal(${lastId})"  style="position: absolute; float:right; z-index: 1; top: 4%; right: -5%;">
                            <span class="ti ti-x"></span>
                        </button>
                        <div>
                            <label for="title" class="form-label mt-3">${serviceName}</label>
                            <input type="text" class="form-control title" id="title" name="servicePageBlocks[${i}].title" placeholder="${serviceName}"
                           maxlength="100">
                        </div>
                        <div>
                            <label for="description" class="form-label mt-3">${serviceDescription}</label>
                            <div id="${descriptionId}" name="servicePageBlocks[${i}].description"></div>
                        </div>
                </div>
        </div>`
    );
    i++;
    initializeQuill(descriptionId);
});

function initializeQuill(id){
    const fullEditor = new Quill("#"+id, {
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
$("#save-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    let formData = collectServiceBlocksData();
    appendIdsToDeleteToFormData(formData);
    appendSeoToFormData(formData);
    for (var pair of formData.entries()) {
        console.log(pair[0]+ ', ' + pair[1]);
    }
    sendData(formData);
});
function collectServiceBlocksData() {
    let formData = new FormData();
    let ind = 0;
    $(".title").each(function () {
        let currentId = $(this).parent().parent().parent().attr("id");
        formData.append("servicePageBlocks["+ind+"].id",currentId);
        let title = $(this).val();
        formData.append("servicePageBlocks["+ind+"].title",title);
        $(this).attr("name", "servicePageBlocks["+ind+"].title");
        let description = descriptions[ind].root.innerHTML;
        formData.append("servicePageBlocks["+ind+"].description",description);
        $("#description"+currentId).attr("name", "servicePageBlocks["+ind+"].description")
        let image = $('#image-input'+currentId).prop('files')[0];
        if(image === undefined){
            formData.append("servicePageBlocks["+ind+"].image", new File([""], "filename"));
            $('#image-input'+currentId).attr("name","servicePageBlocks["+ind+"]");
        } else {
            formData.append("servicePageBlocks["+ind+"].image",image);
        }
        ind++;
    });
    return formData;
}

function appendIdsToDeleteToFormData(formData) {
    console.log(idsToDelete);
    for(let j = 0; j < idsToDelete.length; j++){
        formData.append("idsToDelete["+j+"]",idsToDelete[j]);
    }
}

function appendSeoToFormData(formData) {
    formData.append("seoRequest.seoTitle", $("#seoTitle").val());
    formData.append("seoRequest.seoDescription", $("#seoDescription").val());
    formData.append("seoRequest.seoKeywords", $("#seoKeywords").val());
}
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
            $("#services").empty();
            getServicesPage();
            toastr.success(successMessage);
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}

function setImage (input) {
    var myFile = $(input).prop('files');
    if(validateFile(myFile[0].name) != false){
        $(input).siblings("img").attr("src", window.URL.createObjectURL(myFile[0]));
    } else {
        console.log($(input).parent());
        $(input).parent().append(
            '<p class="text-danger mb-0">' + fileExtension + '</p>'
        );
        $(input).val('');
    }
}
function validateFile(value){
    var ext = value.substring(value.lastIndexOf('.') + 1).toLowerCase();
    if($.inArray(ext, ['png','jpg','jpeg']) == -1 && value != "") {
        return false;
    } else {
        return true;
    }
}

$("#cancel-button").on("click", function () {
    descriptions.length = 0;
    idsToDelete.length = 0;
    $("#services").empty();
    drawServicesPage(defaultServicesPage);
    setSeoFields(defaultServicesPage);
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
