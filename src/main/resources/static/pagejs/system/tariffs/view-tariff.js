const tariffEditPathname = window.location.pathname;
let tariffId = tariffEditPathname.substring(tariffEditPathname.lastIndexOf('/') + 1, tariffEditPathname.length);
let $currentUrl = $('#current-tariff');

$currentUrl.attr('href', $currentUrl.attr('href') + tariffId)
$(document).ready(function () {

    $.ajax({
        url: '../get-tariff-by-id/' + tariffId,
        type: 'get',
        dataType: 'json',
        success: function (response) {
            console.log(response)
            fillInputs(response);
        },
        error: function (error) {
            toastr.error(errorMessage);
        }
    })
});

function fillInputs(tariff) {
    $('.current-tariff').html(` ${tariffTitle} : ${tariff.name} `)
    $('#tariff-name').html(tariff.name);
    $('#tariff-description').html(tariff.description);
    const lastModify = new Date(tariff.lastModify * 1000).toLocaleString()
    $('#tariff-edit-date').html(lastModify);
    let index = 0;
    for (let tariffItem of tariff.tariffItems) {
        addTariffItem(index, tariffItem)
        index++;
    }
}

function addTariffItem(index, item) {
    console.log(index)
    $(`<tr>
          <td>${index + 1}</td>
          <td>${item.service.name}</td>
          <td>${item.service.unitOfMeasurement.name}</td>
          <td>${item.servicePrice}</td>
          <td>${item.currency}</td>
      </tr>`).appendTo('.tariff-items-table tbody');
}
