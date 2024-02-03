let tableLength = 5;
let currentPage = 0;
$(window).on("load", function () {
    getTariffs(currentPage);
})

function getTariffs(page) {
    const getUrl = 'tariffs/get-tariffs?page=' + page + '&pageSize=' + tableLength;

    $.ajax({
        type: 'get',
        url: getUrl,
        dataType: 'json',
        success: function (result) {
            console.log(result);
            currentPage = page;
            clearTableLine();
            $(".card-footer").children().remove();
            drawTable(result);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function drawTable(result) {
    const page = result.pageable.pageNumber;
    for (const tariff of result.content) {

        const lastModify = new Date(tariff.lastModify * 1000).toLocaleString();
        $(`<tr data-href="tariffs/view-tariff/${tariff.id}" class="cursor-pointer">
            <td>${tariff.name}</td>
            <td>${tariff.description}</td>
            <td>${lastModify}</td>
            <td>
              <div class="dropdown">
                <button type="button" class="btn p-0 dropdown-toggle hide-arrow"
                       data-bs-toggle="dropdown">
                    <i class="ti ti-dots-vertical"></i>
                </button>
                <div class="dropdown-menu">
                    <a class="dropdown-item" href="tariffs/edit-tariff/${tariff.id}">
                        <i class="ti ti-pencil me-1"></i>${buttonEdit}
                    </a>
                    <button type="button" class="dropdown-item btn justify-content-start" 
                            data-bs-toggle="modal" data-bs-target="#modalToDelete"
                            onclick="addDeleteEvent(${tariff.id})">
                            <i class="ti ti-trash me-1"></i>${buttonDelete}
                    </button>
                 </div>
              </div>
            </td>
          </tr>`).appendTo("tbody");
    }
    addListenerToRow();

    if (result.totalPages) {
        $('<div class="details-table d-flex gap-2 align-items-center"></div>').appendTo('.card-footer')

        const size = result.size;

        const from = page > 0 ? (page * size) + 1 : 1;
        const to = from + result.numberOfElements - 1;
        const total = result.totalElements;

        if (from === total) {
            $('<div class="dataTables_info"">' +
                'Показано ' + from + ' з ' + total + ' тарифів' +
                '</div>').appendTo(".details-table");
        } else {
            $('<div class="dataTables_info">' +
                'Показано ' + from + '-' + to + ' з ' + total + ' тарифів' +
                '</div>').appendTo(".details-table")
        }

        $('<label class="ms-3">Показати по: </label>' +
            '<div class="selecte-wrapper"><select name="tables_length" class="form-select form-select-sm">\n' +
            '      <option value="2">2</option>\n' +
            '      <option value="5">5</option>\n' +
            '      <option value="10">10</option>\n' +
            '       <option value="20">20</option>\n' +
            '</select> </div>').appendTo(".details-table");

        var $select = $('select[name="tables_length"]');
        $select.on("change", function () {
            tableLength = this.value;
            getTariffs(0);
        });
        $select.val(tableLength);

        if (result.totalPages > 1) {
            showPagination(result.totalPages);

            switch (page) {
                case (result.totalPages - 1):
                    $(".page-item.last").addClass('disabled');
                    $(".page-item.next").addClass('disabled');
                    break;
                case 0:
                    $(".page-item.first").addClass('disabled');
                    $(".page-item.prev").addClass('disabled');
                    break;
            }
        }
    }

    function showPagination(countItems) {
        var paginationList = '<ul class="pagination pagination-sm">\n' +
            '<li class="page-item first">\n' +
            '   <a class="page-link waves-effect" onclick="getTariffs(0)"><i class="ti ti-chevrons-left tf-icon fs-6"></i></a>\n' +
            '</li>\n' +
            '<li class="page-item prev">\n' +
            '   <a class="page-link waves-effect" onclick="getTariffs(' + (page - 1) + ')"><i class="ti ti-chevron-left tf-icon fs-6"></i></a>\n' +
            '</li>\n';
        for (let item = 0; item < countItems; item++) {
            paginationList += '<li class="page-item ' + (page === item ? 'active' : '') + '">\n' +
                '<a class="page-link waves-effect" onclick="getTariffs(' + item + ')">' + (item + 1) + '</a>\n' +
                '</li>';
        }
        paginationList += '<li class="page-item next">\n' +
            '       <a class="page-link waves-effect" onclick="getTariffs(' + (page + 1) + ')"><i class="ti ti-chevron-right tf-icon fs-6"></i></a>\n' +
            '   </li>\n' +
            '      <li class="page-item last">\n' +
            '       <a class="page-link waves-effect" onclick="getTariffs(' + (countItems - 1) + ')"><i class="ti ti-chevrons-right tf-icon fs-6"></i></a>\n' +
            '      </li>\n' +
            '   </ul>\n'

        $(paginationList).appendTo(".card-footer");
    }
}
function addDeleteEvent(tariffId) {
    $('.submit-delete').on('click', function () {
        if (tariffId && tariffId > 0) {

            $.ajax({
                type: 'delete',
                url: 'tariffs/delete/' + tariffId,
                success: function () {
                    $('.close-modal').click()
                    toastr.success(successMessageOnDelete)
                    setTimeout(() => getStaff(currentPage), 400);
                },
                error: function () {
                    $('.close-modal').click()
                    toastr.error(errorMessageOnDelete);

                }
            })
        }
    })
}

function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}