// Sélection d'éléments
const contacts = document.querySelectorAll('.contact');
const chatHeader = document.getElementById('chatHeader');
const chatBody = document.getElementById('chatBody');
const chatFooter = document.getElementById('chatFooter');
const chatName = document.getElementById('chatName');
const msgInput = document.getElementById('messageInput');
const sendBtn = document.querySelector('.send-btn');


// === POPUP CREATION GROUPE ===
const addGroupBtn = document.getElementById('addGroupBtn');
const popup = document.getElementById('groupPopup');
const closePopup = document.getElementById('closePopup');
const createGroup = document.getElementById('createGroup');
const groupsContainer = document.querySelector('.groups');

// Clic sur un contact
contacts.forEach(contact => {
  contact.addEventListener('click', () => {
    const name = contact.querySelector('.name').textContent;
    const avatar = contact.querySelector('img').src;

    chatHeader.classList.remove('hidden');
    chatFooter.classList.remove('hidden');
    chatBody.innerHTML = ''; // Effacer les anciens messages
    chatName.textContent = name;
  });
});

// Envoi message
sendBtn.addEventListener('click', () => {
  const text = msgInput.value.trim();
  if (!text) return;

  const msg = document.createElement('div');
  msg.classList.add('message', 'sent');
  msg.textContent = text;
  chatBody.appendChild(msg);
  msgInput.value = '';
  chatBody.scrollTop = chatBody.scrollHeight;
});


createGroup.addEventListener('click', () => {
  const name = document.getElementById('groupName').value.trim();
  if (!name) return;
  const div = document.createElement('div');
  div.classList.add('group');
  div.innerHTML = `<p class="group-name">${name}</p>`;
  groupsContainer.appendChild(div);
  popup.classList.add('hidden');
  document.getElementById('groupName').value = '';
});

document.addEventListener('DOMContentLoaded', () => {
  const searchInputs = document.querySelectorAll('.search-input');

  searchInputs.forEach(input => {
    const dropdown = input.parentElement.querySelector('.dropdown');

    if (dropdown) {
      input.addEventListener('focus', () => {
        dropdown.style.display = 'block';
      });

      input.addEventListener('blur', () => {
        setTimeout(() => {
          dropdown.style.display = 'none';
        }, 150); // délai pour permettre un clic sur une option
      });
    }
  });
});
