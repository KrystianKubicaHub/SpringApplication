
function createInputField(labelText, type, id) {
    const container = document.createElement('div');
    container.className = 'form-group';

    const label = createLabel(labelText, id);
    const input = document.createElement('input');
    input.type = type;
    input.id = id;
    input.className = 'form-input';

    container.appendChild(label);
    container.appendChild(input);

    return container;
}

function createLabel(text, forId) {
    const label = document.createElement('label');
    label.htmlFor = forId;
    label.textContent = text;
    label.className = 'form-label';
    return label;
}

