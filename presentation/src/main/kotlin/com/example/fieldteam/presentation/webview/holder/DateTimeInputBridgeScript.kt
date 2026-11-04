package com.example.fieldteam.presentation.webview.holder

internal object DateTimeInputBridgeScript {
    val SCRIPT = """
        (function() {
          if (window.__androidDateTimePickerBridgeInstalled) return;
          window.__androidDateTimePickerBridgeInstalled = true;

          const requests = Object.create(null);
          const getBridge = () => window.ExternalApplication;

          const isInputType = (node, type) => node instanceof HTMLInputElement && node.type === type;
          const resolveInputByType = (node, type) => {
            if (isInputType(node, type)) return node;
            return node?.closest?.("input[type='" + type + "']") || null;
          };

          const requestPicker = (input, type) => {
            if (!isInputType(input, type)) return;
            if (input.dataset.androidPickerOpen === '1') return;

            const bridge = getBridge();
            if (!bridge) return;

            const bridgeMethod = type === 'date' ? bridge.onRequestDatePicker : bridge.onRequestTimePicker;
            if (!bridgeMethod) return;

            input.dataset.androidPickerOpen = '1';
            const requestId = 'p_' + Date.now() + '_' + Math.random().toString(36).slice(2);
            requests[requestId] = input;
            bridgeMethod.call(
              bridge,
              requestId,
              input.value || null,
              input.min || null,
              input.max || null
            );
          };

          const openPicker = (event) => {
            const dateInput = resolveInputByType(event.target, 'date');
            const timeInput = resolveInputByType(event.target, 'time');
            const input = dateInput || timeInput;
            if (!input) return;

            event.preventDefault();
            event.stopPropagation();
            requestPicker(input, input.type);
          };

          document.addEventListener('click', openPicker, true);
          document.addEventListener('focusin', openPicker, true);

          const applyValue = (requestId, value) => {
            const input = requests[requestId];
            if (!input) return;

            if (value !== null && value !== undefined) {
              input.value = value;
              input.dispatchEvent(new Event('input', { bubbles: true }));
              input.dispatchEvent(new Event('change', { bubbles: true }));
            }

            input.dataset.androidPickerOpen = '0';
            delete requests[requestId];
          };

          const cancelValue = (requestId) => {
            const input = requests[requestId];
            if (input) input.dataset.androidPickerOpen = '0';
            delete requests[requestId];
          };

          window.__androidApplyDatePickerValue = applyValue;
          window.__androidCancelDatePicker = cancelValue;
          window.__androidApplyTimePickerValue = applyValue;
          window.__androidCancelTimePicker = cancelValue;
        })();
    """.trimIndent()
}
