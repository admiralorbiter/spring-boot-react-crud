import React, { Fragment } from "react";
import MaterialTable from 'material-table';
import { TextField, FormHelperText } from '@material-ui/core';
import TableIcons from "./TableIcons";
import MessageSnackbar from "./MessageSnackbar";

function EditComponent({ columnDef, rowData, onRowDataChange,  ...props }) {

    const errorMessage = this.state.hasError[columnDef.field];
    const hasError = !!errorMessage;
    const type = columnDef.type === 'date' ? 'date' : 'text';

    return (
        <Fragment>
            <TextField
                type={type}
                format="dd/MM/yyyy"
                placeholder={columnDef.title}
                value={!props.value ? '' : props.value}
                onChange={event => props.onChange(event.target.value)}
                error={hasError}
                InputProps={{
                    style: {
                        fontSize: 13,
                    }
                }}
            />
            {hasError && <FormHelperText className='Mui-error'>{errorMessage}</FormHelperText>}
        </Fragment>
    );
}

class UsersTable extends React.Component {

    constructor(props) {
        super(props);

        const editComponent = EditComponent.bind(this);
        this.state = {
            error: false,
            hasError: {},
            columns: [
                {editComponent, field: 'firstName',  title: 'First Name'},
                {editComponent, field: 'lastName',   title: 'Last Name'},
                {editComponent, field: 'birthDate',title: 'Date of Birth', type: 'date', render: this.rawDataToFormattedCellText},
                {editComponent, field: 'email',      title: 'E-Mail'},
                {editComponent, field: 'address',    title: 'Address'}
            ],
            data: []
        };
    }

    rawDataToFormattedCellText = rawData => {
        if (rawData.birthDate) {
            const parts = rawData.birthDate.split('-');
            return <span>{`${parts[2]}/${parts[1]}/${parts[0]}`}</span>
        }
        return <span> </span>;
    };

    handleResponse = (res, resolve, reject, successStatus, successHandler) => {
        switch (res.status) {
            case successStatus: {
                resolve();
                this.resetErrors();
                res.json().then(successHandler, () => successHandler());
                break;
            }
            case 400: {
                reject();
                res.json().then(this.fillErrors);
                break;
            }
            default:
                reject();
                this.setState({
                    error: true,
                    errorMessage: 'An error occurred!'
                });
                console.error(res);
        }
    };

    handleAjaxError = reject => {
        return (error) => {
            reject();
            this.setState({
                error,
                errorMessage: `An error occurred: ${error}`
            });
        }
    };

    addUser = newUser => {
        const data = [...this.state.data];
        data.push(newUser);
        this.setState({ ...this.state, data });
    };

    updateUser = newUser => {
        const data = [...this.state.data].map(user => user.id === newUser.id ? newUser : user);
        this.setState({ ...this.state, data });
    };

    deleteUser = deletedUser => {
        const data = [...this.state.data];
        data.splice(data.indexOf(deletedUser), 1);
        this.setState({ ...this.state, data });
    };

    fillErrors = ({errors}) => this.setState({
        hasError: errors.reduce((acc, error) => {
            acc[error.field] = error.defaultMessage;
            return acc;
        }, {})
    });

    resetErrors = () => this.setState({hasError: {}});

    onRowAdd = newUser =>
        new Promise((resolve, reject) => {

            fetch(`${process.env.REACT_APP_BASE_URL}/users`, {
                method: 'POST',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newUser)
            })
            .then(res => this.handleResponse(res, resolve, reject, 201, this.addUser), this.handleAjaxError(reject))

        });

    onRowUpdate = (newUser, oldData) =>
        new Promise((resolve, reject) => {

            fetch(`${process.env.REACT_APP_BASE_URL}/users/${newUser.id}`, {
                method: 'PUT',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                },
                body: JSON.stringify(newUser)
            })
            .then(res => this.handleResponse(res, resolve, reject, 200, this.updateUser), this.handleAjaxError(reject));
        });

    onRowDelete = deletedUser =>
        new Promise((resolve, reject) => {

            fetch(`${process.env.REACT_APP_BASE_URL}/users/${deletedUser.id}`, {
                method: 'DELETE',
                headers: {
                    'Accept': 'application/json',
                    'Content-Type': 'application/json'
                }
            })
            .then(res => this.handleResponse(res, resolve, reject, 204, () => this.deleteUser(deletedUser)), this.handleAjaxError(reject));
        });

    onMessageClose = () => this.setState({error: false});

    componentDidMount() {
        fetch(`${process.env.REACT_APP_BASE_URL}/users`)
            .then(res => res.json())
            .then(
                (data) => {
                    this.setState({
                        data
                    });
                },
                (error) => {
                    this.setState({
                        error,
                        errorMessage: `An error occurred when loading users data: ${error}`
                    });
                }
            )
    }

    render() {
        return (
            <Fragment>
                {this.state.error && <MessageSnackbar type='error' message={this.state.errorMessage} onClose={this.onMessageClose} />}
                <MaterialTable
                    icons={TableIcons}
                    columns={this.state.columns}
                    data={this.state.data}
                    title='Add new user'
                    options={{
                        actionsColumnIndex: -1,
                        search: false,
                        toolbarButtonAlignment: 'left'
                    }}
                    components={{
                        Pagination: () => null
                    }}
                    localization={{body: {emptyDataSourceMessage: 'No users found. You can start the server with `initial-data` profile to create some example users.'}}}
                    editable={{onRowAdd: this.onRowAdd, onRowUpdate: this.onRowUpdate, onRowDelete: this.onRowDelete}}
                />
            </Fragment>
        );
    }
}

export default UsersTable;