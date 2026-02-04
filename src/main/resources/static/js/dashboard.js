function loadTickets() {
    $.ajax({
        url: "/api/tickets",
        type: "GET",
        success: function (tickets) {
            let html = "<ul>";
            tickets.forEach(t => {
                html += `<li>
                    <b>${t.title}</b> - ${t.status} - ${t.priority}
                </li>`;
            });
            html += "</ul>";

            $("#tickets").html(html);
        },
        error: function () {
            alert("Failed to load tickets");
        }
    });
}