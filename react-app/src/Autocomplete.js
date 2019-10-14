import React, {useState} from 'react';
import PropTypes from 'prop-types';
import deburr from 'lodash/deburr';
import Downshift from 'downshift';
import { makeStyles } from '@material-ui/core/styles';
import TextField from '@material-ui/core/TextField';
import Paper from '@material-ui/core/Paper';
import MenuItem from '@material-ui/core/MenuItem';

function renderInput(inputProps) {
    const { InputProps, classes, ref, ...other } = inputProps;

    return (
        <TextField
            InputProps={{
                inputRef: ref,
                classes: {
                    root: classes.inputRoot,
                    input: classes.inputInput,
                },
                ...InputProps,
            }}
            {...other}
        />
    );
}

renderInput.propTypes = {
    classes: PropTypes.object.isRequired,
    InputProps: PropTypes.object,
};

function renderSuggestion(suggestionProps) {
    const { suggestion, index, itemProps, highlightedIndex, selectedItem } = suggestionProps;
    const isHighlighted = highlightedIndex === index;
    const isSelected = (selectedItem || '').indexOf(suggestion.label) > -1;

    return (
        <MenuItem
            {...itemProps}
            key={suggestion.label}
            selected={isHighlighted}
            component="div"
            style={{
                fontWeight: isSelected ? 500 : 400,
            }}
        >
            {suggestion.label}
        </MenuItem>
    );
}

renderSuggestion.propTypes = {
    highlightedIndex: PropTypes.oneOfType([PropTypes.oneOf([null]), PropTypes.number]).isRequired,
    index: PropTypes.number.isRequired,
    itemProps: PropTypes.object.isRequired,
    selectedItem: PropTypes.string.isRequired,
    suggestion: PropTypes.shape({
        label: PropTypes.string.isRequired,
    }).isRequired,
};

function getSuggestions(suggestions, value, { showEmpty = false } = {}, ) {
    const inputValue = deburr(value.trim()).toLowerCase();
    const inputLength = inputValue.length;
    let count = 0;

    //FIXME I guess I'm expected to ask for each user input
    console.log('getSuggestions', inputValue);

    return inputLength === 0 && !showEmpty
        ? []
        : suggestions.filter(suggestion => {
            const keep =
                count < 5 && suggestion.label.slice(0, inputLength).toLowerCase() === inputValue;

            if (keep) {
                count += 1;
            }

            return keep;
        });
}

const useStyles = makeStyles(theme => ({
    root: {
        flexGrow: 1,
        height: 100,
        width: 300
    },
    container: {
        flexGrow: 1,
        position: 'relative',
    },
    paper: {
        position: 'absolute',
        zIndex: 11, // .MTableHeader-header-165 has 10
        marginTop: theme.spacing(1),
        left: 0,
        right: 0,
    },
    inputRoot: {
        flexWrap: 'wrap',
    },
    inputInput: {
        width: 'auto',
        flexGrow: 1,
    }
}));

export default function Autocomplete() {
    const classes = useStyles();

    const [data, setData] = useState([]);
    const [lastSearch, setLastSearch] = useState('');
    const [lastSearchRequestHandle, setLastSearchRequestHandle] = useState(null);

    const handleSuggestionsRequest = value => {

        if (value === lastSearch) {
            return;
        }

        clearTimeout(lastSearchRequestHandle);
        const handle = setTimeout(function() {

            fetch(`${process.env.REACT_APP_BASE_URL}/users?search=${value}`)
                .then(response => response.json())
                .then(data => setData(data.map(user => ({label: user.fullName}))))
                .then(() => setLastSearch(value));
        }, 500);

        setLastSearchRequestHandle(handle);
    };

    return (
        <div className={classes.root}>
            <Downshift id="downshift-options">
                {({
                      clearSelection,
                      getInputProps,
                      getItemProps,
                      getLabelProps,
                      getMenuProps,
                      highlightedIndex,
                      inputValue,
                      isOpen,
                      openMenu,
                      selectedItem,
                  }) => {
                    const { onBlur, onChange, onFocus, ...inputProps } = getInputProps({
                        onChange: event => {
                            if (event.target.value === '') {
                                clearSelection();
                            }
                            else {
                                handleSuggestionsRequest(event.target.value);
                            }
                        },
                        onFocus: openMenu,
                        placeholder: 'Search users',
                    });

                    return (
                        <div className={classes.container}>
                            {renderInput({
                                fullWidth: true,
                                classes,
                                label: 'Search',
                                InputLabelProps: getLabelProps({ shrink: true }),
                                InputProps: { onBlur, onChange, onFocus },
                                inputProps,
                            })}

                            <div {...getMenuProps()}>
                                {isOpen ? (
                                    <Paper className={classes.paper} square>
                                        {getSuggestions(data, inputValue, { showEmpty: true }, handleSuggestionsRequest).map((suggestion, index) =>
                                            renderSuggestion({
                                                suggestion,
                                                index,
                                                itemProps: getItemProps({ item: suggestion.label }),
                                                highlightedIndex,
                                                selectedItem,
                                            }),
                                        )}
                                    </Paper>
                                ) : null}
                            </div>
                        </div>
                    );
                }}
            </Downshift>
        </div>
    );
}
