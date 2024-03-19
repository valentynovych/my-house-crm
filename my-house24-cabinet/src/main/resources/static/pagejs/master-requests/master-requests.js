let tableLength = 5;
let currentPage = 0;


function getMasterTypeLabel(status) {
    switch (status) {
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
        default:
            return '- - - -'
    }
}

function getMasterRequestStatus(sheetType) {
    switch (sheetType) {
        case "NEW":
            return labelStatusNew;
        case "IN_PROGRESS":
            return labelStatusInProgress
        case "DONE":
            return labelStatusDone
        case "CANCELED":
            return labelStatusCanceled;
        default:
            return '- - - -'
    }
}

$(window).on("load", function () {
    getMasterRequests(currentPage);
})

function addParametersToUrl(url) {
    url.searchParams.append('page', currentPage);
    url.searchParams.append('pageSize', tableLength);
    return url
}

function getMasterRequests(page) {
    currentPage = page;

    blockCardDody();
    let url = new URL('master-requests/get-requests', window.location.origin + window.location.pathname);
    url = addParametersToUrl(url);
    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
            console.log(result)
            clearTableLine();
            $(".card-footer").empty();
            drawTable(result);
        },
        error: function () {
            toastr.error(errorMessage);
        }
    });
}

function drawTable(result) {

    if (result.content && result.content.length > 0) {
        for (const request of result.content) {

            const date = new Date(request.visitDate * 1000);
            const dateString = date.toLocaleString().slice(0, 17);
            const status = getMasterRequestStatus(request.status);
            const statusBadge = function (statusValue) {
                switch (statusValue) {
                    case 'NEW':
                        return `<span class="badge rounded-pill bg-primary">${status}</span>`;
                    case 'IN_PROGRESS':
                        return `<span class="badge rounded-pill bg-info">${status}</span>`;
                    case 'DONE':
                        return `<span class="badge rounded-pill bg-success">${status}</span>`;
                    case 'CANCELED':
                        return `<span class="badge rounded-pill bg-danger">${status}</span>`;
                }
            }
            const isExpiredRequest = date < new Date() && request.status === 'NEW';

            $(`<tr>
                <td class="text-center">${(request.id + '').padStart(3, '000')}</td>
                <td class="text-center">${getMasterTypeLabel(request.masterType)}</td>
                <td>${request.description}</td>
                <td>${dateString}</td>
                <td class="text-center">${statusBadge(request.status)}</td>
                <td class="text-center">
                   <button type="button" class="btn justify-content-start" data-bs-toggle="modal" data-bs-target="#modalToDelete" 
                   onclick="addDeleteEvent(${request.id})"><i class="ti ti-trash me-1"></i>${buttonLabelDelete}</button>
                </td>
            </tr>`
            ).appendTo("tbody");
        }
    } else {
        $(`<tr>
            <td colspan="6" class="text-center fw-bold h4">${emptyTableLabel}</td>
           </tr>`).appendTo('tbody');
    }

    drawPaginationElements(result, 'getMasterRequests')
    drawPagination(result.totalPages, currentPage, 'getMasterRequests');
}

function addDeleteEvent(requestId) {
    $('.submit-delete').on('click', function () {
        $.ajax({
            url: 'master-requests/delete/' + requestId,
            type: 'delete',
            success: function (response) {
                toastr.success(successMessageOnDelete);
                setTimeout(() => getMasterRequests(currentPage), 500);
            }, error: function (error) {
                console.log(error);
                if (error.status === 423) {
                    toastr.error(errorMessageOnDelete);
                } else {
                    toastr.error(errorMessage);
                }
            }
        });
    });
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}