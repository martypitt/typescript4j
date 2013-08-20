var Greeter = (function () {
    function Greeter() {
    }
    Object.defineProperty(Greeter.prototype, "greeting", {
        get: function () {
            return this._greeting;
        },
        enumerable: true,
        configurable: true
    });
    return Greeter;
})();
