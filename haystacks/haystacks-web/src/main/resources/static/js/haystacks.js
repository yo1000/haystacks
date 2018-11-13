var haystacks = haystacks || {}
haystacks.onClickSave = (event) => {
    const clickSaveContainerElm = event.target.closest(".is-data-note-container")
    const savingInputElm = clickSaveContainerElm.querySelector("input")
    savingInputElm.setAttribute("readonly", "readonly")

    const savingFqn = clickSaveContainerElm.dataset.fqn
    const savingInputValue = savingInputElm.value

    clickSaveContainerElm.removeChildren()

    clickSaveContainerElm.appendChild(savingInputElm)
    clickSaveContainerElm.appendChildElm("i", (iElm) => {
        iElm.classList.add("fas")
        iElm.classList.add("fa-circle-notch")
        iElm.classList.add("fa-spin")
    })

    fetch("/api/notes/" + savingFqn, {
        method: "POST",
        body: savingInputValue
    }).then(resp => {
        if (!resp.ok) throw Error(resp.statusText)
        return resp
    }).then(() => {
        clickSaveContainerElm.appendChildNote(savingInputValue)
    }).catch(() => {
        const failedInputElm = clickSaveContainerElm.querySelector("input")
        failedInputElm.removeAttribute("readonly")

        clickSaveContainerElm.removeChildren()

        clickSaveContainerElm.appendChild(failedInputElm)
        clickSaveContainerElm.appendChildElm("a", (aElm) => {
            aElm.appendChildElm("i", (iElm) => {
                iElm.setAttribute("title", "Has not been saved yet")
                iElm.classList.add("fas")
                iElm.classList.add("fa-exclamation-triangle")
            })
            aElm.addEventListener("click", (event) => haystacks.onClickSave(event))
        })
    })
}

Node.prototype.appendChildElm = function(name, func) {
    const node = this.appendChild(document.createElement(name))
    if (func) func(node)
    return node
}

Node.prototype.appendChildTxt = function(text) {
    return this.appendChild(document.createTextNode(text))
}

Node.prototype.removeChildren = function() {
    while (this.firstChild) {
        this.removeChild(this.firstChild);
    }
}

Node.prototype.appendChildNote = function(note) {
    const fragment = new DocumentFragment()
    fragment.appendChildElm("span", (spanElm) => {
        spanElm.appendChildTxt(note)
    })
    fragment.appendChildElm("a", (aElm) => {
        aElm.appendChildElm("i", (iElm) => {
            iElm.classList.add("fas")
            iElm.classList.add("fa-pen")
        })
        aElm.addEventListener("click", (event) => {
            const clickEditContainerElm = event.target.closest(".is-data-note-container")
            const inputValue = clickEditContainerElm.querySelector("span").innerText

            clickEditContainerElm.removeChildren()
            clickEditContainerElm.appendChildElm("input", (inputElm) => {
                inputElm.setAttribute("type", "text")
                inputElm.classList.add("input")
                inputElm.value = inputValue
                inputElm.focus()
                inputElm.addEventListener("keypress", (event) => {
                    if (event.key === "Enter") {
                        haystacks.onClickSave(event)
                    }
                })
            })
            clickEditContainerElm.appendChildElm("a", (aElm) => {
                aElm.appendChildElm("i", (iElm) => {
                    iElm.classList.add("fas")
                    iElm.classList.add("fa-save")
                })
                aElm.addEventListener("click", (event) => haystacks.onClickSave(event))
            })
        })
    })

    this.removeChildren()
    this.appendChild(fragment)
}
