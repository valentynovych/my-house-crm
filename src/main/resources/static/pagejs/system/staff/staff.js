let tableLength = 5;
let currentPage = 0;
let byName = '';
let byRole = '';
let byPhone = '';
let byEmail = '';
let byStatus = '';
let timer;

let $role = $('#filter-by-role');
$role.select2({
    dropdownParent: $('#dropdownParent'),
    placeholder: '',
    minimumResultsForSearch: -1,
    allowClear: true,
    ajax: {
        type: "GET",
        url: 'staff/get-roles',
        processResults: function (response) {
            return {
                results: $.map(response, function (role) {
                    return {
                        id: role.id,
                        text: getRoleLabel(role.name)
                    }
                })
            };
        }
    }
});

let $status = $('#filter-by-status');
$status.select2({
    debug: true,
    dropdownParent: $('#dropdownParent'),
    minimumResultsForSearch: -1,
    placeholder: '',
    allowClear: true,
    ajax: {
        type: "GET",
        url: 'staff/get-statuses',
        dataType: 'json',
        processResults: function (response) {
            return {
                results: $.map(response, function (status) {
                    return {
                        id: status,
                        text: getStatusLabel(status)
                    }
                })
            };
        }
    }
});

$('#filter-by-name').on('input', function () {
    byName = this.value;
    delayBeforeSearch();
})
$role.on('change', function () {
    byRole = this.value;
    delayBeforeSearch();
})
$status.on('change', function () {
    byStatus = this.value;
    delayBeforeSearch();
})
$('#filter-by-phone').on('input', function () {
    byPhone = this.value;
    delayBeforeSearch();
})
$('#filter-by-email').on('input', function () {
    byEmail = this.value;
    delayBeforeSearch();
})

$('.clear-filters').on('click', function () {
    $('#filter-by-role, #filter-by-status')
        .val('').trigger('change');
    $('#filter-by-name, #filter-by-phone, #filter-by-email')
        .val('').trigger('input');
})

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getStaff(0)
    }, keyPause);
}

$(window).on("load", function () {
    getStaff(currentPage);
})

function getStaff(page) {

    blockCardDody();
    let url = new URL('staff/get-staff', window.location.origin + window.location.pathname);
    url.searchParams.append('page', page);
    url.searchParams.append('pageSize', tableLength);
    if (byName) {
        url.searchParams.append('name', byName);
    }
    if (byRole) {
        url.searchParams.append('role', byRole);
    }
    if (byPhone) {
        url.searchParams.append('phone', byPhone);
    }
    if (byEmail) {
        url.searchParams.append('email', byEmail);
    }
    if (byStatus) {
        url.searchParams.append('status', byStatus);
    }

    $.ajax({
        type: 'get',
        url: url,
        dataType: 'json',
        success: function (result) {
            currentPage = page;
            clearTableLine();
            $(".card-footer").children().remove();
            drawTable(result);
        },
        error: function () {
            toastr.error("Упс.. Виникла помилка");
        }
    });
}

function getRoleLabel(role) {
    switch (role) {
        case 'DIRECTOR':
            return roleDirector;
        case 'MANAGER':
            return roleManager;
        case 'ACCOUNTANT':
            return roleAccountant;
        case 'ELECTRICIAN':
            return roleElectrician;
        case 'PLUMBER':
            return rolePlumber;
    }
}

function getStatusLabel(status) {
    switch (status) {
        case 'NEW':
            return statusNew;
        case 'ACTIVE':
            return statusActive;
        case 'DISABLED':
            return statusDisabled;
        default:
            return 'Не відомий'
    }
}

function drawTable(result) {

    if (result.content && result.content.length > 0) {
    const page = result.pageable.pageNumber;
    let iter = 0;
    for (const staff of result.content) {

        const badgeStatus = staff.status === 'NEW'
            ? '<span class="badge bg-label-info me-1">' + statusNew + '</span>'
            : staff.status === 'ACTIVE'
                ? '<span class="badge bg-label-success me-1">' + statusActive + '</span>'
                : '<span class="badge bg-label-danger me-1">' + statusDisabled + '</span>';

        const buttonToDelete = (staff.role.name !== 'DIRECTOR')
            ? '<button type="button" class="dropdown-item btn justify-content-start delete-staff" data-bs-toggle="modal" data-bs-target="#modalToDelete"' +
            '       onclick="addDeleteEvent(' + staff.id + ')">\n' +
            '       <i class="ti ti-trash me-1"></i>' + buttonLabelDelete + '\n' +
            '</button>\n'
            : '';

        $('<tr data-href="staff/view-staff/' + staff.id + '" class="cursor-pointer">\n' +
            '<td>' + ++iter + '</td>' +
            '<td>' + staff.firstName + ' ' + staff.lastName + '</td>\n' +
            '<td>' + getRoleLabel(staff.role.name) + '</td>\n' +
            '<td>' + staff.phoneNumber + '</td>\n' +
            '<td>' + staff.email + '</td>\n' +
            '<td class="text-center">' + badgeStatus + '</td>\n' +
            '<td>\n' +
            '  <div class="dropdown">\n' +
            '   <button type="button" class="btn p-0 dropdown-toggle hide-arrow"\n' +
            '           data-bs-toggle="dropdown">\n' +
            '    <i class="ti ti-dots-vertical"></i>\n' +
            '   </button>\n' +
            '     <div class="dropdown-menu">\n' +
            '       <button type="button" class="dropdown-item btn justify-content-start" onclick="sendInvite(' + staff.id + ')">' +
            '           <i class="ti ti-mail me-1"></i>' + buttonLabelInvite + '</button>' +
            '       <a class="dropdown-item" href="staff/edit-staff/' + staff.id + '">\n' +
            '           <i class="ti ti-pencil me-1"></i>' + buttonLabelEdit + '\n' +
            '       </a>\n' + buttonToDelete +
            '     </div>\n' +
            '  </div>\n' +
            '</td>\n' +
            '</tr>'
        ).appendTo("tbody");
        addListenerToRow();
    }

    drawPaginationElements(result, 'getStaff')
    drawPagination(result.totalPages, page, 'getStaff');
    } else {
        $(`<tr>
            <td colspan="7" class="text-center fw-bold h4">${dataNotFound}</td>
           </tr>`).appendTo('tbody');
    }
}

function addListenerToRow() {
    $('tr[data-href] td:not(:last-child)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function addDeleteEvent(staffId) {
    $('.submit-delete').on('click', function () {
        if (staffId && staffId > 0) {

            $.ajax({
                type: 'delete',
                url: 'staff/delete/' + staffId,
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

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}