let tableLength = 5;
let currentPage = 0;
$(window).on("load", function () {
    getTariffs(currentPage);
})

function getTariffs(page) {
    blockCardDody();
    const getUrl = 'tariffs/get-tariffs?page=' + page + '&pageSize=' + tableLength;

    $.ajax({
        type: 'get',
        url: getUrl,
        dataType: 'json',
        success: function (result) {
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
                    <a class="dropdown-item" href="tariffs/copy-tariff/${tariff.id}">
                        <i class="ti ti-copy me-1"></i>${buttonCopy}
                    </a>
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

    drawPaginationElements(result, 'getTariffs')
    drawPagination(result.totalPages, page, 'getTariffs');

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
                error: function (error) {
                    $('.close-modal').click()
                    if (error.status === 409) {
                        toastr.error(conflictMessageOnDelete.replace('{}',
                            `<span class="fw-bold">${error.responseText}</span>`))
                    } else {
                        toastr.error(errorMessageOnDelete);
                    }

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