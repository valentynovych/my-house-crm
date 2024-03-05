const messageId = window.location.pathname.match(/\d+/g);

$(document).ready(function () {
    blockCardDody();
    $.ajax({
        url: '../get-message/' + messageId,
        type: 'get',
        success: function (response) {
            console.log(response);
            fill(response);
        },
        error: function (error) {
            console.log(error);
            toastr.error(errorMessage);
        }
    })
})

function fill(response) {
    $('#subject').html(response.subject);
    $('#staff').html(`${response.staff.firstName} ${response.staff.lastName}`);
    const date = new Date(response.sendDate * 1000).toLocaleString();
    $('#sendDate').html(date);
    $('.message-text').html(response.text);
}

$('.submit-delete').on('click', function () {
    let formData = new FormData();
    const idArray = [Number(messageId)];
    formData.set("messagesToDelete", idArray);

    $.ajax({
        url: '../delete-messages',
        type: 'delete',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            toastr.success(successMessageOnDelete.replace('{}', $('#subject').html()));
            setTimeout(() => window.history.back(), 500);
        },
        error: function (error) {
            toastr.error(errorMessage);
        }
    });
})
