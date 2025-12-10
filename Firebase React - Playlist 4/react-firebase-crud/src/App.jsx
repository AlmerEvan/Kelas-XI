import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import Write from './components/Write.jsx';
import Read from './components/Read.jsx';
import UpdateRead from './components/UpdateRead.jsx';
import UpdateWrite from './components/UpdateWrite.jsx'; // Impor komponen baru

function App() {
  return (
    <Router>
      <div className="App">
        <h1>Aplikasi CRUD React & Firebase</h1>
        {/* Navigasi sederhana untuk semua operasi */}
        <nav style={{ marginBottom: '20px', display: 'flex', gap: '15px', justifyContent: 'center' }}>
          <a href="/">Create (Tulis)</a>
          <a href="/read">Read (Baca Biasa)</a>
          <a href="/update">Update/Delete (Baca ID)</a>
        </nav>
        <Routes>
          <Route path="/" element={<Write />} />
          <Route path="/write" element={<Write />} />
          <Route path="/read" element={<Read />} />
          {/* Rute untuk Update/Delete */}
          <Route path="/update" element={<UpdateRead />} />
          {/* Rute dinamis untuk pembaruan */}
          <Route path="/update/:id" element={<UpdateWrite />} />
        </Routes>
      </div>
    </Router>
  );
}

export default App;
