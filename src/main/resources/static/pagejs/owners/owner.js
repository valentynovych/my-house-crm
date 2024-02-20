let defaultOwner;
$(document).ready(function () {
    console.log(statusLink);
    initializeInputMasks();
    autosize($("#aboutOwner"));
    initializeStatusSelect();
    var d = new Date(new Date().getFullYear(), 0, 1);
    if(statusLink.includes("..")){
        getOwner();
        $("#breadCrumb").text(breadCrumbEdit);
        $("#pageTitle").text(editOwner)
    } else {
        $("#breadCrumb").text(breadCrumbAdd);
        $("#pageTitle").text(newOwner)
        $("#birthDate").flatpickr({
            locale: "uk",
            dateFormat: "d.m.Y",
            maxDate: moment(d).subtract(17, 'years').format('DD.MM.YYYY')
        });
    }
});

function initializeInputMasks(){
    new Cleave('.phoneNumber', {
        numericOnly: true,
        blocks: [0, 15],
        delimiters: ["+"]
    });
    new Cleave('.viberNumber', {
        numericOnly: true,
        blocks: [0, 15],
        delimiters: ["+"]
    });
    new Cleave('.telegramUsername', {
        blocks: [0, 50],
        delimiters: ["@"]
    });
}

function initializeStatusSelect (){
    $('#status').wrap('<div class="position-relative"></div>').select2({
        language: "uk",
        dropdownParent: $('#status').parent(),
        minimumResultsForSearch: -1,
        ajax: {
            type: "GET",
            url: statusLink,
            processResults: function (response) {
                return {
                    results: $.map(response, function (item) {
                        return {
                            text: getStatus(item),
                            id: item
                        }
                    })
                };
            }

        }
    });
}
function getStatus(status) {
    switch (status) {
        case 'NEW':
            return newStatus;
        case 'ACTIVE':
            return activeStatus;
        case 'DISABLED':
            return disabledStatus;
    }
}
$("#generatePassword").on("click", function () {
    var password = generatePassword();
    $("#password").val(password);
    $("#confirmPassword").val(password);
});

function generatePassword(){
    var passwordLength = Math.random() * (16 - 8) + 8;
    var password = "";
    while(password.length < passwordLength) {
        password += getLowerCase() + getUpperCase()  + getNumber();
    }
    password += getSymbol() + getNumber();
    return password;
}
const types = {
    upperCase: "ABCDEFGHIJKLMNOPQRSTUVWXYZ",
    lowerCase: "abcdefghijklmnopqrstuvwxyz",
    numbers: "0123456789",
    symbols: ",./?"
}
function getUpperCase() {
    return types.upperCase[Math.floor(Math.random() * types.upperCase.length)];
}

function getLowerCase() {
    return types.lowerCase[Math.floor(Math.random() * types.lowerCase.length)];
}

function getNumber() {
    return types.numbers[Math.floor(Math.random() * types.numbers.length)];
}

function getSymbol() {
    return types.symbols[Math.floor(Math.random() * types.symbols.length)];
}


$("#avatar").on("change", function () {
    var myFile = $(this).prop('files');
    if(validateFile(myFile[0].name) != false){
        $("#avatar-img").attr("src", window.URL.createObjectURL(myFile[0]));
    } else {
        $("#avatarValidation").text(fileValidation);
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


$("#save-button").on("click", function () {
    blockCardDody();
    clearAllErrorMessage();
    let formData = collectData();
    sendData(formData);
});

function collectData() {
    let formData = new FormData();
    $("#form").find('input:text, input:password, select, textarea').each(function (){
        if($(this).attr("id").localeCompare('status') !== 0) {
            formData.append($(this).attr("id"), $(this).val());
        }
    });
    var status = $("#status").val() == null? '': $("#status").val();
    formData.append($("#status").attr("id"), status);
    formData.append('avatar', $('#avatar').prop('files')[0]);
    return formData;
}
function sendData(formData) {
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: formData,
        contentType: false,
        processData: false,
        success: function (response) {
            window.location.href = response;
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}

function getOwner(){
    blockCardDody();
    let url = window.location.pathname;
    let id = url.substring(url.lastIndexOf('/') + 1);
    $.ajax({
        type: "GET",
        url: "get-owner/"+id,
        success: function (response) {
            console.log(response);
            defaultOwner = response;
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
        if(key.localeCompare("birthDate") !== 0)
            $("#" + key).val(value);
    })
    const currentUrl = window.location.href;
    const myArray = currentUrl.split("/");
    let root = myArray[3];
    $("#avatar-img").attr("src",'/'+root+ '/uploads/'+response.image);
    var d = new Date(new Date().getFullYear(), 0, 1);
    $("#birthDate").flatpickr({
        locale: "uk",
        defaultDate: response.birthDate,
        dateFormat: "d.m.Y",
        maxDate: moment(d).subtract(17, 'years').format('DD.MM.YYYY')
    });
    var option = new Option(getStatus(response.status), response.status, true, true);
    $('#status').append(option).trigger('change');
}
$("#cancel-button").on("click", function () {
    blockBy("#form");
    $("#form").find('input:text, input:password, input:file, select, textarea').val('');
    if(defaultOwner === undefined){
        $("#avatar-img").attr("src", "https://static.vecteezy.com/system/resources/previews/004/141/669/non_2x/no-photo-or-blank-image-icon-loading-images-or-missing-image-mark-image-not-available-or-image-coming-soon-sign-simple-nature-silhouette-in-frame-isolated-illustration-vector.jpg");
    } else {
        setFields(defaultOwner);
    }
    unblockBy("#form");
})