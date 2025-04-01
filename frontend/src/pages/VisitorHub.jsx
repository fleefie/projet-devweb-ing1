import React from 'react';
import { Link } from 'react-router-dom';

const VisitorHub = () =>{
    console.log("Dans VisitorHub");
    return (
        <div>
          <p>Salut jeune visiteur, tu n'es pas connecté</p>
          <ul>
            <li>
              <Link to="/login">Login</Link>
            </li>
          </ul>
        </div>
      );
};

export default VisitorHub;