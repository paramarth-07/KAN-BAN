/**
 * Playful Geometric Kanban Interactive Engine
 * Handles HTML5 drag-and-drop, optimistic UI updates, CSRF token handling, modals, and full-window confetti celebrations.
 */

document.addEventListener('DOMContentLoaded', () => {
    initDragAndDrop();
    initTaskEditModal();
    initToasts();
    initStepperConfetti();
    checkInitialDoneCelebration();
});

function getCsrfInfo() {
    const tokenMeta = document.querySelector('meta[name="_csrf"]');
    const headerMeta = document.querySelector('meta[name="_csrf_header"]');
    return {
        token: tokenMeta ? tokenMeta.getAttribute('content') : '',
        header: headerMeta ? headerMeta.getAttribute('content') : 'X-CSRF-TOKEN'
    };
}

function showToast(message, type = 'success') {
    let toastContainer = document.getElementById('playful-toast-container');
    if (!toastContainer) {
        toastContainer = document.createElement('div');
        toastContainer.id = 'playful-toast-container';
        toastContainer.style.position = 'fixed';
        toastContainer.style.bottom = '24px';
        toastContainer.style.right = '24px';
        toastContainer.style.zIndex = '9999';
        toastContainer.style.display = 'flex';
        toastContainer.style.flexDirection = 'column';
        toastContainer.style.gap = '10px';
        document.body.appendChild(toastContainer);
    }

    const toast = document.createElement('div');
    toast.className = `playful-alert ${type === 'success' ? 'playful-alert-success' : 'playful-alert-danger'}`;
    toast.style.margin = '0';
    toast.style.boxShadow = '4px 4px 0px #1E293B';
    toast.style.animation = 'popIn 0.25s cubic-bezier(0.34, 1.56, 0.64, 1)';
    toast.innerHTML = `
        <i class="fa-solid ${type === 'success' ? 'fa-circle-check' : 'fa-circle-exclamation'}"></i>
        <span>${message}</span>
    `;

    toastContainer.appendChild(toast);

    setTimeout(() => {
        toast.style.opacity = '0';
        toast.style.transition = 'opacity 0.3s ease';
        setTimeout(() => toast.remove(), 300);
    }, 3500);
}

function updateColumnCounts() {
    document.querySelectorAll('.kanban-column').forEach(col => {
        const dropzone = col.querySelector('.column-dropzone');
        const badge = col.querySelector('.column-count-badge');
        if (dropzone && badge) {
            const count = dropzone.querySelectorAll('.task-card').length;
            badge.textContent = count;
        }
    });
}

/**
 * Playful Geometric Full-Window Confetti System
 * Dynamically sizes to fill the entire window viewport and bursts 200+ particles
 * with Playful Geometric shapes (circles, squares, pills, triangles) and vibrant tokens.
 */
function firePlayfulConfetti() {
    let canvas = document.getElementById('playful-confetti-canvas');
    if (!canvas) {
        canvas = document.createElement('canvas');
        canvas.id = 'playful-confetti-canvas';
        canvas.style.position = 'fixed';
        canvas.style.top = '0';
        canvas.style.left = '0';
        canvas.style.width = '100vw';
        canvas.style.height = '100vh';
        canvas.style.pointerEvents = 'none';
        canvas.style.zIndex = '99999';
        document.body.appendChild(canvas);
    }

    const ctx = canvas.getContext('2d');
    let width = canvas.width = window.innerWidth;
    let height = canvas.height = window.innerHeight;

    const handleResize = () => {
        width = canvas.width = window.innerWidth;
        height = canvas.height = window.innerHeight;
    };
    window.addEventListener('resize', handleResize);

    const colors = [
        '#8B5CF6', // Vivid Violet
        '#F472B6', // Hot Pink
        '#FBBF24', // Amber/Yellow
        '#34D399', // Mint/Emerald
        '#38BDF8', // Sky Blue
        '#F97316', // Orange
        '#EC4899'  // Fuchsia
    ];

    const shapes = ['square', 'circle', 'pill', 'triangle'];
    const particleCount = 200;
    const particles = [];

    // Launch cannons from left and right bottom corners towards center and top
    for (let i = 0; i < particleCount; i++) {
        const fromLeft = i % 2 === 0;
        const originX = fromLeft ? width * 0.15 : width * 0.85;
        const originY = height * 0.82;
        
        const angle = fromLeft 
            ? (Math.PI / 4) + (Math.random() * 0.6 - 0.3) // ~45 deg
            : (3 * Math.PI / 4) + (Math.random() * 0.6 - 0.3); // ~135 deg
        
        const speed = 15 + Math.random() * 19;

        particles.push({
            x: originX,
            y: originY,
            vx: Math.cos(angle) * speed * (fromLeft ? 1 : -1),
            vy: -Math.sin(angle) * speed,
            gravity: 0.46 + Math.random() * 0.18,
            drag: 0.965,
            size: 8 + Math.random() * 9,
            shape: shapes[Math.floor(Math.random() * shapes.length)],
            color: colors[Math.floor(Math.random() * colors.length)],
            rotation: Math.random() * 360,
            rotationSpeed: (Math.random() - 0.5) * 14,
            opacity: 1,
            fadeSpeed: 0.005 + Math.random() * 0.004,
            wobble: Math.random() * 10,
            wobbleSpeed: 0.08 + Math.random() * 0.05
        });
    }

    let animationFrameId;
    const startTime = performance.now();

    function render(time) {
        ctx.clearRect(0, 0, width, height);
        let activeCount = 0;

        for (let p of particles) {
            p.x += p.vx;
            p.y += p.vy;
            p.vx *= p.drag;
            p.vy = (p.vy * p.drag) + p.gravity;
            p.rotation += p.rotationSpeed;
            p.opacity -= p.fadeSpeed;
            p.wobble += p.wobbleSpeed;

            if (p.opacity > 0 && p.y < height + 60) {
                activeCount++;
                ctx.save();
                ctx.globalAlpha = Math.max(0, p.opacity);
                ctx.translate(p.x + Math.sin(p.wobble) * 4, p.y);
                ctx.rotate((p.rotation * Math.PI) / 180);
                ctx.fillStyle = p.color;
                ctx.strokeStyle = '#1E293B';
                ctx.lineWidth = 1.5;

                if (p.shape === 'circle') {
                    ctx.beginPath();
                    ctx.arc(0, 0, p.size / 2, 0, Math.PI * 2);
                    ctx.fill();
                    ctx.stroke();
                } else if (p.shape === 'pill') {
                    const w = p.size * 1.6;
                    const h = p.size * 0.8;
                    ctx.beginPath();
                    if (ctx.roundRect) {
                        ctx.roundRect(-w / 2, -h / 2, w, h, 6);
                    } else {
                        ctx.rect(-w / 2, -h / 2, w, h);
                    }
                    ctx.fill();
                    ctx.stroke();
                } else if (p.shape === 'triangle') {
                    ctx.beginPath();
                    ctx.moveTo(0, -p.size / 1.5);
                    ctx.lineTo(p.size / 1.5, p.size / 1.5);
                    ctx.lineTo(-p.size / 1.5, p.size / 1.5);
                    ctx.closePath();
                    ctx.fill();
                    ctx.stroke();
                } else {
                    // square
                    ctx.fillRect(-p.size / 2, -p.size / 2, p.size, p.size);
                    ctx.strokeRect(-p.size / 2, -p.size / 2, p.size, p.size);
                }

                ctx.restore();
            }
        }

        if (activeCount > 0 && (time - startTime < 4500)) {
            animationFrameId = requestAnimationFrame(render);
        } else {
            ctx.clearRect(0, 0, width, height);
            window.removeEventListener('resize', handleResize);
            if (canvas.parentElement) {
                canvas.parentElement.removeChild(canvas);
            }
        }
    }

    animationFrameId = requestAnimationFrame(render);
}

function checkInitialDoneCelebration() {
    if (document.getElementById('trigger-task-done-celebration')) {
        setTimeout(() => {
            firePlayfulConfetti();
        }, 150);
    }
}

function initStepperConfetti() {
    // Detect when stepping from In Progress to Done via button
    document.querySelectorAll('.kanban-column#col-in-progress .btn-card-step[title*="Done"]').forEach(btn => {
        btn.addEventListener('click', () => {
            firePlayfulConfetti();
        });
    });
}

function initDragAndDrop() {
    const cards = document.querySelectorAll('.task-card');
    const dropzones = document.querySelectorAll('.column-dropzone');

    cards.forEach(card => {
        card.setAttribute('draggable', 'true');

        card.addEventListener('dragstart', (e) => {
            card.classList.add('dragging');
            e.dataTransfer.setData('text/plain', card.dataset.id);
            e.dataTransfer.effectAllowed = 'move';
        });

        card.addEventListener('dragend', () => {
            card.classList.remove('dragging');
            dropzones.forEach(zone => zone.classList.remove('drag-over'));
        });
    });

    dropzones.forEach(zone => {
        zone.addEventListener('dragover', (e) => {
            e.preventDefault();
            e.dataTransfer.dropEffect = 'move';
            zone.classList.add('drag-over');
        });

        zone.addEventListener('dragleave', (e) => {
            if (!zone.contains(e.relatedTarget)) {
                zone.classList.remove('drag-over');
            }
        });

        zone.addEventListener('drop', async (e) => {
            e.preventDefault();
            zone.classList.remove('drag-over');

            const taskId = e.dataTransfer.getData('text/plain');
            const card = document.querySelector(`.task-card[data-id="${taskId}"]`);
            if (!card) return;

            const targetStatus = zone.dataset.status;
            const originalZone = card.parentElement;
            const originalStatus = originalZone.dataset.status;

            if (targetStatus === originalStatus) {
                return;
            }

            // Remove any empty-state-box in target dropzone
            const emptyBox = zone.querySelector('.empty-state-box');
            if (emptyBox) {
                emptyBox.style.display = 'none';
            }

            // Optimistic move
            zone.appendChild(card);
            updateColumnCounts();

            // Trigger full-window confetti animation when moved to DONE!
            if (targetStatus === 'DONE') {
                firePlayfulConfetti();
            }

            // Send async update to server
            const csrf = getCsrfInfo();
            try {
                const response = await fetch(`/api/tasks/${taskId}/status`, {
                    method: 'POST',
                    headers: {
                        'Content-Type': 'application/json',
                        [csrf.header]: csrf.token
                    },
                    body: JSON.stringify({
                        status: targetStatus,
                        position: zone.querySelectorAll('.task-card').length - 1
                    })
                });

                if (!response.ok) {
                    throw new Error(`Server returned status ${response.status}`);
                }

                const data = await response.json();
                showToast(`Moved to ${data.statusDisplayName}`, 'success');
            } catch (err) {
                // Revert on failure
                originalZone.appendChild(card);
                updateColumnCounts();
                showToast('Failed to update task status: ' + err.message, 'error');
            }
        });
    });
}

function initTaskEditModal() {
    const editModalEl = document.getElementById('editTaskModal');
    if (!editModalEl) return;

    document.querySelectorAll('.btn-open-edit-task').forEach(btn => {
        btn.addEventListener('click', (e) => {
            const card = btn.closest('.task-card');
            if (!card) return;

            const taskId = card.dataset.id;
            const title = card.dataset.title;
            const desc = card.dataset.description;
            const status = card.dataset.status;
            const priority = card.dataset.priority;
            const dueDate = card.dataset.dueDate;
            const boardId = card.dataset.boardId;

            const form = document.getElementById('editTaskForm');
            if (form) {
                form.action = `/boards/${boardId}/tasks/${taskId}/edit`;
                const idInput = form.querySelector('[name="id"]');
                const titleInput = form.querySelector('[name="title"]');
                const descInput = form.querySelector('[name="description"]');
                const statusSelect = form.querySelector('[name="status"]');
                const prioritySelect = form.querySelector('[name="priority"]');
                const dueDateInput = form.querySelector('[name="dueDate"]');

                if (idInput) idInput.value = taskId;
                if (titleInput) titleInput.value = title || '';
                if (descInput) descInput.value = desc || '';
                if (statusSelect) statusSelect.value = status || 'TODO';
                if (prioritySelect) prioritySelect.value = priority || 'MEDIUM';
                if (dueDateInput) dueDateInput.value = dueDate || '';
            }
        });
    });
}

function initToasts() {
    // Auto-dismiss static alert banners after 5 seconds
    setTimeout(() => {
        document.querySelectorAll('.playful-alert-auto-dismiss').forEach(el => {
            el.style.opacity = '0';
            el.style.transition = 'opacity 0.4s ease';
            setTimeout(() => el.remove(), 400);
        });
    }, 4500);
}
