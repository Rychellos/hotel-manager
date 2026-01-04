import {Home} from "./home/home";
import {Test} from "./test";
import {Route} from "./navbar/navbar";

export const routes: Route[] = [
    {
        path: '',
        component: Home,
        data: {name: "Homepage"}
    },
    {
        path: 'test',
        component: Test,
        data: {name: 'Test'}
    },
];
