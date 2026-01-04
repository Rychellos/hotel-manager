import {Component} from '@angular/core';
import {RouterLink, RouterLinkActive, Routes} from "@angular/router";
import {routes} from "../app.routes";

export type Route = Routes[number] & {
    data: { name: string }
};

@Component({
    selector: 'app-navbar',
    imports: [
        RouterLink,
        RouterLinkActive
    ],
    templateUrl: './navbar.html',
    styleUrl: './navbar.css',
})
export class Navbar {

    protected readonly routes: Route[] = routes;
}
