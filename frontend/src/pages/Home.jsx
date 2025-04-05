import React, { useState } from 'react';
import JsonBuilder from '../components/builders/JsonBuilder';

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

  <div className="json-builder-page">
      <JsonBuilder 
        onChange={handleJsonChange} 
        initialValue={initialJson}
        title="Test builder JSON"
      />
  </div>
  </div>
);
};

export default Home;
