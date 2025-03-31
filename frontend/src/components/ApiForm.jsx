import React, { useState } from 'react';

const ApiForm = ({ fields, onSubmit, submitLabel = 'Submit' }) => {
  const [formData, setFormData] = useState(
    fields.reduce((acc, { name, initialValue = '' }) => {
      acc[name] = initialValue;
      return acc;
    }, {})
  );
  const [fieldErrors, setFieldErrors] = useState({});
  const [serverMessage, setServerMessage] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({ ...prev, [name]: value }));
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    setFieldErrors({});
    setServerMessage('');
    try {
      const response = await onSubmit(formData);
      if (response && response.data && response.data.message) {
        setServerMessage(response.data.message);
      }
    } catch (err) {
      if (err.response) {
        if (err.response.data?.fieldErrors) {
          setFieldErrors(err.response.data.fieldErrors);
        }
        if (err.response.data?.message) {
          setServerMessage(err.response.data.message);
        }
      }
    }
  };

  return (
    <form onSubmit={handleSubmit}>
      {serverMessage && <div className="server-message">{serverMessage}</div>}
      {fields.map(({ name, label, type = 'text' }) => (
        <div key={name}>
          <label>{label}</label>
          <input name={name} type={type} value={formData[name]} onChange={handleChange} />
          {fieldErrors[name] && <span className="error">{fieldErrors[name]}</span>}
        </div>
      ))}
      <button type="submit">{submitLabel}</button>
    </form>
  );
};

export default ApiForm;

