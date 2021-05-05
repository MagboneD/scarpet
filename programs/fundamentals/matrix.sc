//A library of functions to create and manipulate matrices. These are as of now just lists of lists of numbers, but with these functions can act as matrices
//This is of course an abstract library, which will no doubt be replaces by a better native scarpet implementation in the future, but it will do for now.
//This may be temporary in case gnembon adds matrices into scarpet as a built in data structure, but nothing is certain so far.
//By: Ghoulboy

create_matrix(width, height)->(//creates an empty matrix (so all 0 values)
    matrix = [];
    loop(height,
        matrix: _= [];
        w = _;
        loop(width,
            matrix:w:_ = 0
        );
    );
    matrix
);

_create_matrix(...columns)->( //creating matrix a bunch of columns, or as a singleton list of columns
    if(is_matrix(columns),
        matrix = columns,
        check_matrix(columns:0); //if this throws an error (i.e that is not a matrix) then it will fail, if not return null and continue as normal
        matrix = columns:0
    );
    matrix
);

is_matrix(matrix)->(//returns boolean, true if given value is a matrix and false otherwise

    try(//cos scarpet try doesn't actually catch errors, only when you call throw() function

        if(type(matrix) == 'list' && length(matrix)>0 && type(matrix:0)=='list',//doing easy checks first, so it fails early without having to do excess computation.

            //checking if all columns are the same length
            col_size = length(matrix:0);
            if(col_size==0, throw());//checking that it's not got empty columns (cos that technically passes the next check)

            //checking if all the items are numbers
            loop(length(matrix),
                row = _;

                if(length(matrix:_)!=col_size, throw()); //checking that all columns are the same length

                loop(col_size,
                    if(type(matrix:row:_)!='number',
                        throw()
                    )
                )
            ),

            throw()
        ),
        return(false)
    );
    true
);

is_vector(vector)->//checking like this so the first check fails without having to iterate over the whole list potentially
    !(type(vector)!='list' || length(vector)==0 || first(vector, type(_)!='number'));

check_matrix(matrix)->( //checks if given value is a matrix, and if not throws an error
    if(!is_matrix(matrix),// todo decide whether or not to give more detailed error, i.e what exactly went wrong
        exit(print('Operation is invalid, "' + matrix + '" is not a matrix'))
    )
);


identity(size)->(//returns identity matrix of given size
    identity = [];
    loop(size,
        width = _;
        identity:_=[];
        loop(size, //cos its always a square matrix
            identity:width:_ = width ==_
        )
    );
    identity
);

get_column(matrix, column)->( //get_row func would be identical to matrix:row, whereas here you need that element of each list, which is O(n)
    check_matrix(matrix);
    if(length(matrix:0)<=column,
        exit(print(str('Index out of bounds, cannot access %s column of %s by %s matrix', column, length(matrix), length(matrix:0))))
    );
    col = [];
    loop(length(matrix),
        col:_ = matrix:_:column
    );
    col
);

matrix_add(a, b) ->( //returns a new matrix whose values are the sum of the two input matrices
    check_matrix(a);
    check_matrix(b);

    if(length(a)!=length(b) || length(a:0)!=length(b:0),
        exit(print('Cannot add uneven matrices'))
    );

    sum = [];

    loop(length(a),
        width = _;
        sum:width=[];
        loop(length(a:0),
            sum:width:_ = a:width:_ + b:width:_
        )
    );
    sum
);

matrix_subtract(a, b) ->( //returns a new matrix whose values are the difference of the two input matrices (i.e a - b)
    check_matrix(a);
    check_matrix(b);

    if(length(a)!=length(b) || length(a:0)!=length(b:0),
        exit(print('Cannot subtract uneven matrices'))
    );

    sum = [];

    loop(length(a),
        width = _;
        sum:width=[];
        loop(length(a:0),
            sum:width:_ = a:width:_ - b:width:_
        )
    );
    sum
);

//This is complicated, because you can do many things here, depending if input is scalar, vector or matrix:
//  scalar * scalar = error (y doin that op here)
//  scalar * vector = error (y doin that op here)
//  scalar * matrix = error (cannot do it in this order). Still gotta handle.
//  vector * scalar = error (y doin that op here)
//  vector * vector = matrix with rows same as length of first vector, columns same as that of second.
//  vector * matrix = error unless vector length is equal to matrix rows (so treating vector as 1*m matrix, where m is rows of the matrix with which ur multiplying), in which case it's a vector
//  matrix * scalar = matrix scaled by scalar
//  matrix * vector = error unless vector length is equal to matrix columns (so treating vector as 1*n matrix, where n is columns of the matrix with which ur multiplying), in which case it's a vector
//  matrix * matrix = regular matrix operation.

matrix_multiply(a, b) ->(
    if(type(a)=='list', //if not a list, must be a scalar
        result = [];
        if(is_vector(a), // vector * something
            if(is_vector(b),//vector * vector = matrix
                loop(length(a),
                    width = _;
                    result:_=[];
                    loop(length(b),
                        result:width:_ = a:width * b:_
                    )
                ),
                is_matrix(b),//vector * matrix = vector
                if(length(a)==length(b),
                    loop(length(b),
                        row=_;
                        result:row = reduce(b:row, _a + (_ * a:row),0);
                    ),
                    exit(print(str('Cannot multiply vector %s by matrix %s in this order, they have mismatched sizes', a, b)))
                ),
                exit(print('Why are you doing that operation here?'))
            ),//in case they inputted a list which wasn't a vector.
            is_matrix(a), //matrix * something
            if(is_vector(b), //matrix * vector = vector
                
            ),
            exit(print(a + ' is not a valid vector or matrix'))
        ),
        exit(print(if(is_matrix(b),
            'Cannot multiply a scalar by a matrix in that order (try the other way around)',
            'Why are you doing that operation here?'
        )))
    );
    result
);