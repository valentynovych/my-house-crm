const currHref = window.location.pathname;
const basePath = currHref.split('/').slice().splice(0, 3).join('/');
let pageUnreadMessages = 0;
let pageSizeUnreadMessages = 5;
$('.menu-item a').each(function (i, item) {
    if (currHref === ($(item).attr('href'))) {
        $(item).parent().addClass('active')
        $(this).parents('[data-parrent]').addClass("active open");
    }
});

$(document).ready(function () {

    $.ajax({
        url: `${basePath}/messages/get-unread-messages?page=${pageUnreadMessages}&pageSize=${pageSizeUnreadMessages}`,
        type: 'GET',
        success: (data) => {
            buildUnreadMessagesList(data);
        },
        error: () => {
            toastr.error(errorMessage);
        }
    });
});

function buildUnreadMessagesList(messages) {
    let idsToMarkAsRead = [];
    const messagesCount = messages.totalElements;
    const $messagesCount = $('#messages-count');
    const $newMessagesCountText = $('#new-messages-count-text');
    const messagesList = $('#unreadMessagesList');
    messagesList.empty();

    if (messagesCount === 0) {
        $newMessagesCountText.html(newMessagesNotExistsLabel);
    } else {
        $messagesCount.removeClass('d-none');
        $messagesCount.html(`${messagesCount}`);
        $newMessagesCountText.html(`${newMessagesLabel}: ${messagesCount}`);
        messages.content.forEach((message) => {
                messagesList.append(buildMessageItem(message));
                idsToMarkAsRead.push(message.id);
            }
        );
        addListenersToUnreadMessages(idsToMarkAsRead);
    }
}

function buildMessageItem(message) {
    const lineText = message.text.replace(/<[^>]*>/g, ' ');
    return `<li class="list-group-item list-group-item-action dropdown-notifications-item">
                <a href="${basePath}/messages/view-message/${message.id}" class="menu-link">
                    <span class="fw-bold">${message.subject}</span> - ${lineText}
                </a>
            </li>`;
}

function addListenersToUnreadMessages(idsToMarkAsRead) {

    $('#mark-read-all').on('click', function () {
        const data = {
            idsToMarkAsRead: idsToMarkAsRead
        };

        $.ajax({
            url: `${basePath}/messages/read-all-messages`,
            type: 'POST',
            contentType: 'application/json',
            data: JSON.stringify(data),
            success: () => {
                toastr.success();
                buildUnreadMessagesList({totalElements: 0, content: []});
            },
            error: () => {
                toastr.error(errorMessage);
            }
        });

    });
}
