let defaultContactsPage;
$(document).ready(function () {
    initializeInputMask();
    initializeAutosize();
    getContacts();
});
function initializeInputMask() {
    new Cleave('.phoneNumber', {
        numericOnly: true,
        blocks: [0, 15],
        delimiters: ["+"]
    });
}
function initializeAutosize() {
    autosize($("#mapCode"));
    autosize($("#seoDescription"));
    autosize($("#seoKeywords"));
}
function getContacts() {
    blockCardDody();
    $.ajax({
        type: "GET",
        url: "contacts-page/get",
        success: function (response) {
            console.log(response);
            defaultContactsPage = response;
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
        $("#" + key).val(value);
    });
    const convertedText = fullEditor.clipboard.convert(response.text);
    fullEditor.setContents(convertedText, 'silent');
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
    formData.append("text", fullEditor.root.innerHTML);
    for (var pair of formData.entries()) {
        console.log(pair[0]+ ', ' + pair[1]);
    }
    return formData;
}
function sendData(formData) {
    console.log("here");
    $.ajax({
        type: "POST",
        url: window.location.href,
        data: formData,
        contentType: false,
        processData: false,
        success: function () {
            getContacts();
            toastr.success(successMessage);
        },
        error: function (error) {
            printErrorMessageToField(error);
        }
    });
}
$("#cancel-button").on("click", function () {
    blockBy("#form");
    $("#form").find('input:text, textarea').val('');
    fullEditor.setText("");
    setFields(defaultContactsPage);
    unblockBy("#form");
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

const fullEditor = new Quill('#text', {
    bounds: '#full-editor',
    placeholder: 'Type Something...',
    modules: {
        formula: true,
        toolbar: fullToolbar
    },
    theme: 'snow'
});