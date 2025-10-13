        function scrollToBottom() {
            const chatHistory = document.getElementById("chatHistory");
            if (chatHistory) {
                chatHistory.scrollTop = chatHistory.scrollHeight;
            }
        }

        // Appelé automatiquement après chaque mise à jour AJAX
        jsf.ajax.addOnEvent(function(data) {
            if (data.status === "success") {
                scrollToBottom();
            }
        });