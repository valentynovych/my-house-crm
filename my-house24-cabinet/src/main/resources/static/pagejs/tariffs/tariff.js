const apartmentId = window.location.pathname.match(/\d+$/g);

$(document).ready(function () {
    blockCardDody();
    $.ajax({
        url: '../tariffs/get-apartment-tariff/' + apartmentId,
        type: 'get',
        success: function (response) {
            console.log(response);
            drawTable(response);
        },
        error: function (error) {
            console.log(error);
            toastr.error(errorMessage);
        }
    })
});

function drawTable(response) {
    $('tbody').empty();
    let tariffTitle = $('#tariff-title');
    tariffTitle.text(`${tariffTitle.text()} - ${response.name}`);
    for (const tariffItem of response.tariffItems) {
        $(`<tr >
                <td>${tariffItem.serviceName}</td>
                <td>${tariffItem.unitOfMeasurementName}</td>
                <td>${tariffItem.servicePrice} ${currency}</td>
               </tr>`
        ).appendTo("tbody");
    }
}