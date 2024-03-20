let currentPage = 0;
let tableLength = 5;
let timer;

let byText;
let checkedInputs = 0;
let rowCount = 0;

const topCheckbox = document.querySelector("#checked-all-top");
const bottomCheckbox = document.querySelector("#checked-all-bottom");
const $filterByText = $('#filter-by-text');

$filterByText.on('input', function () {
    byText = this.value;
    delayBeforeSearch();
})

topCheckbox.addEventListener('change', function () {
    applyCheckedUnchecked(this.checked);
})
bottomCheckbox.addEventListener('change', function () {
    applyCheckedUnchecked(this.checked);
});

function applyCheckedUnchecked(isChecked) {
    toagleTopBottomCheckboxes(isChecked);
    $('input:checkbox[data-id]').prop('checked', isChecked);
    isChecked ? checkedInputs = rowCount : checkedInputs = 0;
}

function toagleTopBottomCheckboxes(isChecked) {
    topCheckbox.checked = isChecked;
    bottomCheckbox.checked = isChecked;
}

function delayBeforeSearch() {
    let keyPause = 400;
    clearTimeout(timer);
    timer = setTimeout(function () {
        getMessages(0)
    }, keyPause);
}

$(document).ready(function () {
    blockCardDody();
    getMessages(0);
});

function getMessages(page) {
    toagleTopBottomCheckboxes(false);
    blockCardDody();
    currentPage = page;
    let url = new URL('messages/get-messages', window.location.origin + window.location.pathname);
    url = addParametersToUrl(url);
    $.ajax({
        url: url,
        type: 'get',
        success: function (response) {
            clearTableLine();
            $(".card-footer").empty();
            drawTable(response);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function addParametersToUrl(url) {
    url.searchParams.append('page', currentPage);
    url.searchParams.append('pageSize', tableLength);
    if (byText) url.searchParams.append('text', byText);
    return url;
}

function checkAllChecked() {
    toagleTopBottomCheckboxes(rowCount === checkedInputs);
}

function drawTable(result) {
    console.log(result)
    if (result.content && result.content.length > 0) {
        for (const message of result.content) {
            const sendDate = new Date(message.sendDate * 1000).toLocaleString().slice(0, 17);
            const lineText = message.text.replace(/<[^>]*>/g, ' ');
            $(`<tr data-href="messages/view-message/${message.id}" ${!message.isRead ? 'class="bg-label-primary"' : ''}">
                <td>
                    <input class="form-check-input m-auto" type="checkbox" 
                    id="to-delete-${message.id}" data-id="${message.id}">
                </td>
                <td class="fw-bold cursor-pointer">${message.staffFullName}</td>
                <td class="text-message cursor-pointer">
                    <span class="fw-bold">${message.subject}</span> - ${lineText}
                </td>
                <td class="cursor-pointer">${sendDate}</td>
               </tr>`
            ).appendTo("tbody");
            rowCount++;
        }
        addListenerToRow();
        $('input:checkbox[data-id]').on('change', function () {
            this.checked ? checkedInputs++ : checkedInputs--;
            checkAllChecked();
        });
    } else {
        $(`<tr>
            <td colspan="4" class="text-center fw-bold h4">${emptyTableLabel}</td>
           </tr>`).appendTo('tbody');
    }

    drawPaginationElements(result, 'getMessages')
    drawPagination(result.totalPages, currentPage, 'getMessages');
}

function addListenerToRow() {
    $('tr[data-href]  td:nth-child(n + 2)').on('click', function () {
        window.location = $(this).parent().attr('data-href');
    })
}

function clearTableLine() {
    $("tbody").find("tr").each(function () {
        this.remove();
    });
}

$('.submit-delete').on('click', function () {
    const elementNodeListOf = document.querySelectorAll('input[type="checkbox"][data-id]:checked');
    if (elementNodeListOf.length > 0) {
        let ids = [];
        elementNodeListOf.forEach(checkbox => ids.push(checkbox.getAttribute("data-id")));
        deleteMessages(ids);
    } else {
        toastr.warning(messagesNotSelectedItem)
    }
});

function deleteMessages(messageIds) {
    blockCardDody();
    let formData = new FormData();
    formData.set("messagesToDelete", messageIds);

    $.ajax({
        url: 'messages/delete-messages',
        type: 'delete',
        processData: false,
        contentType: false,
        data: formData,
        success: function (response) {
            getMessages(currentPage);
            const successMessageWithSubjects = successMessageOnDelete.replace('{}', getSubjectDeletedMessages(messageIds));
            toastr.success(successMessageWithSubjects);
        },
        error: function (error) {
            console.log(error);
        }
    })
}

function getSubjectDeletedMessages(messageIds) {
    let subjects = '';
    for (let i = 0; i < messageIds.length; i++) {
        const closest = $(`[data-id="${messageIds[i]}"]`).closest('tr');
        subjects += closest.find('.subject').html();
        if (i < messageIds.length - 1) {
            subjects += ', '
        }
    }
    return subjects;
}
