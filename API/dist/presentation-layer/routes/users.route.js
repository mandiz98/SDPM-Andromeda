"use strict";
var __decorate = (this && this.__decorate) || function (decorators, target, key, desc) {
    var c = arguments.length, r = c < 3 ? target : desc === null ? desc = Object.getOwnPropertyDescriptor(target, key) : desc, d;
    if (typeof Reflect === "object" && typeof Reflect.decorate === "function") r = Reflect.decorate(decorators, target, key, desc);
    else for (var i = decorators.length - 1; i >= 0; i--) if (d = decorators[i]) r = (c < 3 ? d(r) : c > 3 ? d(target, key, r) : d(target, key)) || r;
    return c > 3 && r && Object.defineProperty(target, key, r), r;
};
var __metadata = (this && this.__metadata) || function (k, v) {
    if (typeof Reflect === "object" && typeof Reflect.metadata === "function") return Reflect.metadata(k, v);
};
var __awaiter = (this && this.__awaiter) || function (thisArg, _arguments, P, generator) {
    function adopt(value) { return value instanceof P ? value : new P(function (resolve) { resolve(value); }); }
    return new (P || (P = Promise))(function (resolve, reject) {
        function fulfilled(value) { try { step(generator.next(value)); } catch (e) { reject(e); } }
        function rejected(value) { try { step(generator["throw"](value)); } catch (e) { reject(e); } }
        function step(result) { result.done ? resolve(result.value) : adopt(result.value).then(fulfilled, rejected); }
        step((generator = generator.apply(thisArg, _arguments || [])).next());
    });
};
Object.defineProperty(exports, "__esModule", { value: true });
const awilix_express_1 = require("awilix-express");
let UserRoutes = class UserRoutes {
    constructor({ userManager }) {
        this.userManager = userManager;
    }
    getUsers(req, res) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                res.json(yield this.userManager.getUsers());
            }
            catch (error) {
                console.log(error);
                res.json(error);
            }
        });
    }
    getUser(req, res) {
        return __awaiter(this, void 0, void 0, function* () {
            try {
                res.json(yield this.userManager.getUser(req.params.id));
            }
            catch (error) {
                console.log(error);
                res.json(error);
            }
        });
    }
};
__decorate([
    awilix_express_1.route("/"),
    awilix_express_1.GET(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], UserRoutes.prototype, "getUsers", null);
__decorate([
    awilix_express_1.route("/:id"),
    awilix_express_1.GET(),
    __metadata("design:type", Function),
    __metadata("design:paramtypes", [Object, Object]),
    __metadata("design:returntype", Promise)
], UserRoutes.prototype, "getUser", null);
UserRoutes = __decorate([
    awilix_express_1.route("/users"),
    __metadata("design:paramtypes", [Object])
], UserRoutes);
exports.default = UserRoutes;
//# sourceMappingURL=users.route.js.map