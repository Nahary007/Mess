// Sélection d'éléments (supprimé contacts et sendBtn, car géré par JSF AJAX)

// === POPUP CREATION GROUPE === (inchangé)
const addGroupBtn = document.getElementById('addGroupBtn');
const popup = document.getElementById('groupPopup');
const closePopup = document.getElementById('closePopup');
const createGroup = document.getElementById('createGroup');
const groupsContainer = document.querySelector('.groups');

// Création de groupe (inchangé)
if (createGroup) {
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
}

// Changement : Gestion du dropdown UNIQUEMENT pour la sidebar droite (inchangé)
document.addEventListener('DOMContentLoaded', () => {
  const rightSearchInput = document.querySelector('.sidebar.right .search-input');

  if (rightSearchInput) {
    const dropdown = rightSearchInput.parentElement.querySelector('.dropdown');

    if (dropdown) {
      rightSearchInput.addEventListener('focus', () => {
        dropdown.style.display = 'block';
      });

      rightSearchInput.addEventListener('blur', () => {
        setTimeout(() => {
          dropdown.style.display = 'none';
        }, 150);
      });
    }
  }

  // Changement : Fonction pour scroller auto après AJAX (appelée via onevent)
  window.scrollToBottom = function(event) {
    if (event.status === 'success') {
      const chatBody = document.getElementById('chatBody');
      if (chatBody) {
        chatBody.scrollTop = chatBody.scrollHeight;
      }
    }
  };
});