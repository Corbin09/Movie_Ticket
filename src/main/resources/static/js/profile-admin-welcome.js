// Wrap everything in a function that can be called when DOM is ready
function initProfileManagement() {
    console.log('Initializing profile management');

    // Cache DOM elements
    const userSection = document.getElementById('userSection');
    const profileOverlay = document.getElementById('profileOverlay');
    const viewProfileContainer = document.getElementById('viewProfileContainer');
    const editProfileContainer = document.getElementById('editProfileContainer');
    const showEditProfile = document.getElementById('showEditProfile');
    const cancelEdit = document.getElementById('cancelEdit');
    const updateProfile = document.getElementById('updateProfile');
    const profileForm = document.getElementById('profileForm');
    const closeProfile = document.getElementById('closeProfile');
    const closeEditProfile = document.getElementById('closeEditProfile');
    const avatarInput = document.getElementById('avatar-input');

    // Ensure overlay is hidden initially
    if (profileOverlay) {
        profileOverlay.style.display = 'none';
    }

    console.log('DOM elements cached');

    // Profile overlay functions
    function toggleProfileOverlay() {
        if (profileOverlay) {
            if (profileOverlay.style.display === 'flex') {
                hideProfileOverlay();
            } else {
                showProfileOverlay();
            }
        }
    }

    function showProfileOverlay() {
        if (profileOverlay) {
            profileOverlay.style.display = 'flex';
            document.body.classList.add('overlay-active');
            console.log('Profile overlay shown');

            // Default to showing view profile first
            if (viewProfileContainer && editProfileContainer) {
                viewProfileContainer.style.display = 'block';
                editProfileContainer.style.display = 'none';
            }
        }
    }

    function hideProfileOverlay() {
        if (profileOverlay) {
            profileOverlay.style.display = 'none';
            document.body.classList.remove('overlay-active');
            resetUpdateButton();
            console.log('Profile overlay hidden');
        }
    }

    // Reset update button state
    function resetUpdateButton() {
        const updateBtn = document.getElementById('updateProfile');
        if (updateBtn) {
            updateBtn.innerHTML = 'Update Profile';
            updateBtn.disabled = false;
        }
    }

    // Handle avatar preview
    window.previewAvatar = function(event) {
        const file = event.target.files[0];
        if (file) {
            // Check file size (max 5MB)
            if (file.size > 5 * 1024 * 1024) {
                showNotification('Image file size should not exceed 5MB', false);
                event.target.value = '';
                return;
            }

            const reader = new FileReader();
            reader.onload = function(e) {
                const avatarImg = document.getElementById('avatar-img');
                if (avatarImg) {
                    avatarImg.src = e.target.result;
                }
            };
            reader.readAsDataURL(file);
        }
    }

    // Form validation
    function clearAllErrors() {
        const errorElements = document.querySelectorAll('.error-message');
        errorElements.forEach(element => {
            element.textContent = '';
            element.style.display = 'none';
        });
    }

    function showError(elementId, message) {
        const errorElement = document.getElementById(elementId);
        if (errorElement) {
            errorElement.textContent = message;
            errorElement.style.display = 'block';
        }
    }

    function showNotification(message, isSuccess) {
        const notification = document.getElementById('updateNotification');
        if (notification) {
            notification.textContent = message;
            notification.className = 'notification ' + (isSuccess ? 'success' : 'error');
            notification.style.display = 'block';

            // Auto-hide success notifications after 3 seconds
            if (isSuccess) {
                setTimeout(() => {
                    notification.style.display = 'none';
                }, 3000);
            }
        }
    }

    function validateProfileForm() {
        let isValid = true;
        clearAllErrors();

        // Validate username
        const username = document.getElementById('editUsername');
        if (username) {
            const usernameValue = username.value.trim();
            if (!usernameValue) {
                showError('usernameError', 'Username is required');
                isValid = false;
            } else if (usernameValue.length < 3) {
                showError('usernameError', 'Username must be at least 3 characters');
                isValid = false;
            }
        }

        // Validate email
        const email = document.getElementById('editEmail');
        if (email) {
            const emailValue = email.value.trim();
            const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
            if (!emailValue) {
                showError('emailError', 'Email is required');
                isValid = false;
            } else if (!emailRegex.test(emailValue)) {
                showError('emailError', 'Please enter a valid email address');
                isValid = false;
            }
        }

        // Validate phone number
        const phone = document.getElementById('editPhone');
        if (phone) {
            const phoneValue = phone.value.trim();
            const phoneRegex = /^\d{10,}$/;
            if (phoneValue && !phoneRegex.test(phoneValue)) {
                showError('phoneError', 'Please enter a valid phone number (at least 10 digits)');
                isValid = false;
            }
        }

        // Validate avatar file if one is selected
        const avatarInput = document.getElementById('avatar-input');
        if (avatarInput && avatarInput.files.length > 0) {
            const file = avatarInput.files[0];
            if (file.size > 5 * 1024 * 1024) {
                showNotification('Image file size should not exceed 5MB', false);
                isValid = false;
            }
        }

        return isValid;
    }

    function updateUserProfile() {
        // Hide previous notifications
        const notification = document.getElementById('updateNotification');
        if (notification) {
            notification.style.display = 'none';
        }

        if (!validateProfileForm()) {
            return;
        }

        // Show loading state on button
        const updateBtn = document.getElementById('updateProfile');
        if (updateBtn) {
            updateBtn.innerHTML = '<span class="loading-spinner"></span> Updating...';
            updateBtn.disabled = true;
        }

        // Prepare form data
        const formData = new FormData();

        // Add form fields
        const username = document.getElementById('editUsername');
        if (username) {
            formData.append('username', username.value.trim());
        }

        const email = document.getElementById('editEmail');
        if (email) {
            formData.append('email', email.value.trim());
        }

        const phone = document.getElementById('editPhone');
        if (phone && phone.value.trim()) {
            formData.append('phoneNumber', phone.value.trim());
        }

        const sex = document.getElementById('editSex');
        if (sex) {
            formData.append('sex', sex.value);
        }

        const dob = document.getElementById('editDateOfBirth');
        if (dob && dob.value) {
            formData.append('dateOfBirth', dob.value);
        }

        // Add avatar file if selected
        const avatarInput = document.getElementById('avatar-input');
        if (avatarInput && avatarInput.files.length > 0) {
            formData.append('userImg', avatarInput.files[0]);
        }

        // Add CSRF token if available
        const csrfToken = document.querySelector('input[name="_csrf"]');
        if (csrfToken) {
            formData.append('_csrf', csrfToken.value);
        }

        console.log('Submitting profile update...');

        // Set timeout for long requests (20 seconds)
        const timeoutId = setTimeout(() => {
            resetUpdateButton();
            showNotification('Request timed out. The server might be processing your image or unavailable.', false);
        }, 20000);

        // Send AJAX request
        fetch('/user/update-profile', {
            method: 'POST',
            body: formData,
            credentials: 'same-origin'
        })
        .then(response => {
            clearTimeout(timeoutId);
            console.log('Server response status:', response.status);

            if (!response.ok) {
                return response.text().then(text => {
                    console.log('Error response text:', text);
                    try {
                        return JSON.parse(text);
                    } catch (e) {
                        console.error('Failed to parse error response:', e);
                        if (response.status === 403) {
                            throw new Error('Access denied. You may need to log in again.');
                        } else {
                            throw new Error('Server returned an error: ' + response.status);
                        }
                    }
                });
            }

            return response.text().then(text => {
                console.log('Success response text:', text);
                try {
                    return JSON.parse(text);
                } catch (e) {
                    console.error('Failed to parse success response:', e);
                    throw new Error('Invalid response format');
                }
            });
        })
        .then(data => {
            console.log('Parsed response data:', data);

            // Handle server validation errors
            if (data.success === false) {
                if (data.errors) {
                    // Show field-specific errors
                    Object.keys(data.errors).forEach(field => {
                        const errorField = field + 'Error';
                        showError(errorField, data.errors[field]);
                    });
                    throw new Error('Please fix the errors above');
                } else {
                    throw new Error(data.message || 'Update failed');
                }
            }

            // Success case
            resetUpdateButton();
            showNotification('Profile updated successfully!', true);

            // Update view profile with new data
            updateViewProfile();

            // Update header username if it was changed
            const headerUsername = document.querySelector('.username');
            const editUsernameInput = document.getElementById('editUsername');
            if (headerUsername && editUsernameInput) {
                headerUsername.textContent = editUsernameInput.value;
            }

            // Update avatars if returned in response
            if (data.user && data.user.userImg) {
                const avatarUrl = data.user.userImg;
                // Update all avatar instances
                const avatars = document.querySelectorAll('.user-avatar, #viewProfileAvatar, #avatar-img');
                avatars.forEach(avatar => {
                    avatar.src = avatarUrl;
                });
            }

            // Switch back to view profile after success
            setTimeout(() => {
                if (editProfileContainer && viewProfileContainer) {
                    editProfileContainer.style.display = 'none';
                    viewProfileContainer.style.display = 'block';
                }
            }, 2000);
        })
        .catch(error => {
            console.error('Profile update error:', error);
            clearTimeout(timeoutId);
            resetUpdateButton();
            showNotification(error.message || 'Failed to update profile. Please try again.', false);
        });
    }

    // Update view profile from edit form
    function updateViewProfile() {
        // Update username
        const viewUsername = document.getElementById('viewUsername');
        const editUsername = document.getElementById('editUsername');
        if (viewUsername && editUsername) {
            viewUsername.value = editUsername.value;
        }

        // Update email
        const viewEmail = document.getElementById('viewEmail');
        const editEmail = document.getElementById('editEmail');
        if (viewEmail && editEmail) {
            viewEmail.value = editEmail.value;
        }

        // Update phone
        const viewPhone = document.getElementById('viewPhone');
        const editPhone = document.getElementById('editPhone');
        if (viewPhone && editPhone) {
            viewPhone.value = editPhone.value;
        }

        // Update sex
        const viewSex = document.getElementById('viewSex');
        const editSex = document.getElementById('editSex');
        if (viewSex && editSex) {
            viewSex.value = editSex.value;
        }

        // Update date of birth with formatting
        const editDate = document.getElementById('editDateOfBirth');
        const viewDob = document.getElementById('viewDateOfBirth');
        if (editDate && viewDob && editDate.value) {
            const parts = editDate.value.split('-');
            if (parts.length === 3) {
                const formattedDate = `${parts[2]}/${parts[1]}/${parts[0]}`;
                viewDob.value = formattedDate;
            }
        }
    }

    // Reset edit form from view form
    function resetEditForm() {
        const viewUsername = document.getElementById('viewUsername');
        const editUsername = document.getElementById('editUsername');
        if (viewUsername && editUsername) {
            editUsername.value = viewUsername.value;
        }

        const viewEmail = document.getElementById('viewEmail');
        const editEmail = document.getElementById('editEmail');
        if (viewEmail && editEmail) {
            editEmail.value = viewEmail.value;
        }

        const viewPhone = document.getElementById('viewPhone');
        const editPhone = document.getElementById('editPhone');
        if (viewPhone && editPhone) {
            editPhone.value = viewPhone.value;
        }

        const viewSex = document.getElementById('viewSex');
        const editSex = document.getElementById('editSex');
        if (viewSex && editSex) {
            // Use selectedIndex to match the option
            for (let i = 0; i < editSex.options.length; i++) {
                if (editSex.options[i].value === viewSex.value) {
                    editSex.selectedIndex = i;
                    break;
                }
            }
        }

        // Convert dd/MM/yyyy to yyyy-MM-dd for date input
        const viewDob = document.getElementById('viewDateOfBirth');
        const editDob = document.getElementById('editDateOfBirth');
        if (viewDob && editDob && viewDob.value) {
            const parts = viewDob.value.split('/');
            if (parts.length === 3) {
                const formattedDate = `${parts[2]}-${parts[1].padStart(2, '0')}-${parts[0].padStart(2, '0')}`;
                editDob.value = formattedDate;
            }
        }

        // Reset avatar file input
        const avatarInput = document.getElementById('avatar-input');
        if (avatarInput) {
            avatarInput.value = '';
        }

        // Clear errors and notifications
        clearAllErrors();
        const notification = document.getElementById('updateNotification');
        if (notification) {
            notification.style.display = 'none';
        }
    }

    // Initialize event listeners
    if (userSection) {
        userSection.addEventListener('click', function(e) {
            // If clicked on logout button, don't show profile
            if (e.target.classList.contains('logout-btn') || e.target.closest('.logout-btn')) {
                return;
            }
            toggleProfileOverlay(); // Changed to toggle function
        });
    }

    // Close profile overlay handlers
    if (closeProfile) {
        closeProfile.addEventListener('click', function() {
            hideProfileOverlay();
        });
    }

    if (closeEditProfile) {
        closeEditProfile.addEventListener('click', function() {
            hideProfileOverlay();
        });
    }

    // Close overlay when clicking background
    if (profileOverlay) {
        profileOverlay.addEventListener('click', function(e) {
            if (e.target === profileOverlay) {
                hideProfileOverlay();
            }
        });
    }

    // Show edit profile view
    if (showEditProfile) {
        showEditProfile.addEventListener('click', function() {
            if (viewProfileContainer && editProfileContainer) {
                viewProfileContainer.style.display = 'none';
                editProfileContainer.style.display = 'block';
                clearAllErrors();

                // Reset notification
                const notification = document.getElementById('updateNotification');
                if (notification) {
                    notification.style.display = 'none';
                }
            }
        });
    }

    // Cancel edit and return to view profile
    if (cancelEdit) {
        cancelEdit.addEventListener('click', function() {
            if (editProfileContainer && viewProfileContainer) {
                editProfileContainer.style.display = 'none';
                viewProfileContainer.style.display = 'block';
                resetEditForm();
            }
        });
    }

    // Update profile button handler
    if (updateProfile) {
        updateProfile.addEventListener('click', function(e) {
            e.preventDefault();
            updateUserProfile();
        });
    }

    // Handle pressing Enter key in form fields
    if (profileForm) {
        profileForm.addEventListener('keypress', function(e) {
            if (e.key === 'Enter' && e.target.tagName !== 'TEXTAREA') {
                e.preventDefault();
                updateProfile && updateProfile.click();
            }
        });
    }

    // Setup avatar file input handler
    if (avatarInput) {
        avatarInput.addEventListener('change', window.previewAvatar);
    }

    // Initialize view profile data on page load
    const editSex = document.getElementById('editSex');
    const viewSex = document.getElementById('viewSex');
    if (editSex && viewSex && editSex.value) {
        viewSex.value = editSex.value;
    }

    const editDob = document.getElementById('editDateOfBirth');
    const viewDob = document.getElementById('viewDateOfBirth');
    if (editDob && viewDob && editDob.value) {
        const parts = editDob.value.split('-');
        if (parts.length === 3) {
            const formattedDate = `${parts[2]}/${parts[1]}/${parts[0]}`;
            viewDob.value = formattedDate;
        }
    }

    console.log('Profile management event listeners initialized');
}

// Execute the function when DOM is ready
document.addEventListener('DOMContentLoaded', function() {
    console.log('DOM content loaded, starting profile management');
    // Check if elements exist before initializing
    if (document.getElementById('userSection') && document.getElementById('profileOverlay')) {
        initProfileManagement();
    } else {
        console.log('Required elements not found, profile management not initialized');
    }
});

// Make the function available globally so it can be called from HTML if needed
window.initProfileManagement = initProfileManagement;