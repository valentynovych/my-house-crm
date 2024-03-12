let i = 0;
let defaultTemplateSettings;
let idsToDelete = [];
let deleteButton;
$(document).ready(function () {
    getInvoiceTemplates();
});
function getInvoiceTemplates() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "templates-settings/get",
        success: function (response) {
            console.log(response);
            $("#savedTemplates").empty();
            defaultTemplateSettings = response;
            showSavedTemplates(response);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}
function showSavedTemplates(response) {
    for(let template of response){
        $("#savedTemplates").append(
            `<div class="mt-4" id="${template.id}">
                <h5>${template.name+" "+ isDefault(template.isDefault)}</h5>
                <div class="d-flex justify-content-start gap-3">
                    <a href="templates-settings/download-template/${template.file}" type="button" class="btn rounded-pill btn-label-info" >
                        <span class="me-1 ti ti-download"></span>
                        ${downloadButton}
                    </a>
                    <button type="button" class="btn rounded-pill btn-label-success" 
                    onclick="setDefault(this)">
                        ${setDefaultButton}
                    </button>
                    <button type="button" class="btn rounded-pill btn-label-danger" 
                    onclick="openDeleteModal(this)">
                        <span class="me-1 ti ti-trash"></span>
                        ${deleteTemplateButton}
                    </button>
                </div>
            </div>`
        );
    }
}
function setDefault(setDefaultButton) {
    let templateDiv = $(setDefaultButton).parent().parent();
    let id = $(templateDiv).attr("id");
    $.ajax({
        type: "POST",
        url: "templates-settings/set-default/"+id,
        success: function () {
            getInvoiceTemplates();
            toastr.success(successMessage);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });

}
function openDeleteModal(thisDelete){
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
                        <button type="button" class="btn btn-danger" id="delete-button" onclick="deleteTemplate()">
                            ${modalDeleteButton}
                        </button>
                    </div>
                </div>
            </div>
        </div>`
        )
    }
    $('#deleteModal').modal('show');
    deleteButton = thisDelete;
}
function deleteTemplate() {
    let templateDiv = $(deleteButton).parent().parent();
    let id = $(templateDiv).attr("id");
    if(id !== undefined){
        idsToDelete.push(id);
    }
    $(templateDiv).remove();
    console.log(idsToDelete);
    $('#deleteModal').modal('hide');
    toastr.success(deleteSuccessful);
}
$("#add-template").on("click", function () {
    $("#newTemplates").append(
    `<div class="row mt-3">
        <div class="col-md-7">
            <div>
                <label for="name" class="form-label">${templateNameLabel}</label>
                <input type="text" class="form-control" id="invoiceTemplates[${i}].name" name="invoiceTemplates[${i}].name"
                       placeholder="${templateNameLabel}" maxlength="100">
            </div>
            <div class="mt-3">
                <div class="row g-3 align-items-center">
                    <div class="col-auto">
                        <input type="file" accept=".xsl" class="form-control d-none"  id="invoiceTemplates[${i}].file" name="invoiceTemplates[${i}]" onchange="setFileName(this)">
                            <label class="input-group-text col-form-label" for="invoiceTemplates[${i}].file">${imageLabel}</label>
                    </div>
                    <div class="col-auto">
                        <p class="my-2 fileName"></p>
                    </div>
                </div>
            </div>
        </div>
        <div class="col-md-5 mt-3 d-flex align-items-center">
            <button type="button" class="btn btn-icon btn-label-danger" onclick="openDeleteModal(this)">
                 <span class="ti ti-trash"></span>
            </button>
        </div>
    </div>`);
    i++;
});
function isDefault(isDefault) {
    console.log(isDefault);
    return isDefault? "("+defaultText+")":"";
}
function setFileName(input) {
    $(input).parent().find(".text-danger").remove();
    var myFile = $(input).prop('files');
    if(validateFile(myFile[0].name) !== false){
        let fileName = $(input).prop("files")[0].name;
        $(input).parent().parent().find(".fileName").text(fileName);
    } else {
        $(input).parent().append(
            '<p class="text-danger mb-0">' + fileExtension + '</p>'
        );
        $(input).val('');
    }
}
function validateFile(value){
    var ext = value.substring(value.lastIndexOf('.') + 1).toLowerCase();
    if($.inArray(ext, ['xsl']) == -1 && value != "") {
        return false;
    } else {
        return true;
    }
}

$("#save-button").on("click", function () {
    if($("#savedTemplates").children().length == 0 && $("#newTemplates").children().length == 0){
        toastr.warning(addWarning);
    } else {
        let formData = collectData();
        sendData(formData);
    }
});

function collectData() {
    let formData = new FormData();
    let j = 0;
    $("#newTemplates").find('input:text').each(function () {
        formData.append("invoiceTemplates["+j+"].name", $(this).val());
        $(this).attr("name", "invoiceTemplates["+j+"].name");
        let fileInput = $(this).parent().parent().find('input:file');
        let file = $(fileInput).prop("files")[0];
        if(file === undefined){
            formData.append("invoiceTemplates["+j+"].file", new File([""], "filename"));
        } else {
            formData.append("invoiceTemplates["+j+"].file", file);
        }
        $(fileInput).attr("name", "invoiceTemplates["+j+"].file");
        j++;
    });
    formData.append("idsToDelete[]", idsToDelete);
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
            idsToDelete.length = 0;
            $("#newTemplates").empty();
            getInvoiceTemplates();
            toastr.success(successMessage);
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}












