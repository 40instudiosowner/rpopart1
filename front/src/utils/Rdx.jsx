import Utils from "./Utils";
import {applyMiddleware, combineReducers, createStore} from "redux";
import {createLogger} from "redux-logger/src";
import {connect} from 'react-redux'
const userConstants = {
    LOGIN: 'USER_LOGIN',
    LOGOUT: 'USER_LOGOUT',
};

export const userActions = {
    login,
    logout
};

const alertConstants = {
    ERROR: 'ERROR',
    CLEAR: 'CLEAR',
};

function login(user) {
    Utils.saveUser(user)
    return { type: userConstants.LOGIN, user }
}

function logout() {
    Utils.removeUser()
    return { type: userConstants.LOGOUT }
}


let user =  Utils.getUser()
const initialState = user ? { user } : {}

function authentication(state = initialState, action) {
    console.log("authentication")
    switch (action.type) {
        case userConstants.LOGIN:
            return { user: action.user };
        case userConstants.LOGOUT:
            return { };
        default:
            return state
    }
}


/* STORE */

const rootReducer = combineReducers({
    authentication,alert
});

const loggerMiddleware = createLogger();

export const store = createStore(
    rootReducer,
    applyMiddleware(loggerMiddleware)
);

export const alertActions = {
    error,
    clear
};

function error(msg) {
    return { type: alertConstants.ERROR, msg }
}

function clear() {
    return { type: alertConstants.CLEAR }
}

function alert(state = {}, action) {
    console.log("alert")
    switch (action.type) {
        case alertConstants.ERROR:
            return { msg: action.msg };
        case alertConstants.CLEAR:
            return { };
        default:
            return state
    }
}





