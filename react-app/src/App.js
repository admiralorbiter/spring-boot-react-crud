import React from 'react';
import CssBaseline from '@material-ui/core/CssBaseline';
import Typography from '@material-ui/core/Typography';
import { makeStyles } from '@material-ui/core/styles';
import Container from '@material-ui/core/Container';
import UsersTable from './UsersTable';
import Autocomplete from "./Autocomplete";

const useStyles = makeStyles(theme => ({
    heroContent: {
        backgroundColor: theme.palette.background.paper,
        padding: theme.spacing(8, 0, 6),
    },
    tableContainer: {
        paddingTop: theme.spacing(8),
        paddingBottom: theme.spacing(8),
    }
}));

function App() {
    const classes = useStyles();

    return (
        <React.Fragment>
            <CssBaseline />
            <main>
                <div className={classes.heroContent}>
                    <Container>
                        <Typography component="h1" variant="h2" align="center" color="textPrimary">
                            Users
                        </Typography>
                    </Container>
                </div>
                <Container className={classes.tableContainer}>
                    <Autocomplete />
                    <UsersTable />
                </Container>
            </main>
        </React.Fragment>
    );
}

export default App;
