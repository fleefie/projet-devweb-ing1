import React, { useState } from 'react';

const Home = () => {
 
const [jsonOutput, setJsonOutput] = useState({});
  
const handleJsonChange = (newJson) => {
    setJsonOutput(newJson);
    console.log('JSON updated:', jsonOutput);
};
  
const initialJson = {
    name: "Product",
    price: 19.99,
    inStock: true,
    tags: []
};

   
    return (
  <div>
    <h1 style={{ textAlign: 'center'}}>Home Page</h1>
    <p>Welcome to your space.</p>
  </div>
);
};

export default Home;
