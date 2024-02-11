let url = window.location.pathname;
let id = url.substring(url.lastIndexOf('/') + 1);
$(document).ready(function () {
    $("#current-owner-link").attr("href","../edit/"+id);
    getOwnerForView();
});

function getOwnerForView() {
    $.ajax({
        type: "GET",
        url: "get/"+id,
        success: function (response) {
            console.log(response);
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
        if(key.localeCompare("status") === 0){
            $("#" + key).append(getStatusSpan(value));
        } else {
            $("#" + key).text(value);
        }
    })
    const currentUrl = window.location.href;
    const myArray = currentUrl.split("/");
    let root = myArray[3];
    $("#avatar-img").attr("src",'../../../uploads/'+response.avatar);
}
function getStatusSpan(status){
    switch (status) {
        case 'NEW':
            return '<span class="badge bg-label-info">'+ newStatus +'</span>';
        case 'ACTIVE':
            return '<span class="badge bg-label-success">'+ activeStatus +'</span>';
        case 'DISABLED':
            return '<span class="badge bg-label-danger">'+ disabledStatus +'</span>';
    }
}