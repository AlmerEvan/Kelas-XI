import React, { useState } from 'react';
// Import db dan fungsi Firebase yang diperlukan
import { db } from '../firebaseConfig'; 
import { ref, push, set } from 'firebase/database';
import { useNavigate } from 'react-router-dom'; // Import useNavigate

const Write = () => {
    const navigate = useNavigate(); // Inisialisasi hook

    // State untuk menyimpan data form
    const [formData, setFormData] = useState({
        name: '',
        age: ''
    });

    // Handler untuk memperbarui state saat input berubah
    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    // Fungsi untuk mengirim data ke Firebase (CREATE)
    const handleSubmit = async (e) => {
        e.preventDefault();
        
        try {
            // 1. Tentukan path/referensi di Firebase Realtime Database
            const dataRef = ref(db, 'data/records');
            
            // 2. Gunakan push() untuk mendapatkan referensi kunci unik baru
            const newRecordRef = push(dataRef);
            
            // 3. Gunakan set() untuk menulis data ke referensi kunci unik tersebut
            await set(newRecordRef, {
                name: formData.name,
                age: formData.age,
                // Tambahkan timestamp opsional
                timestamp: Date.now() 
            });

            // Feedback sukses dan reset form
            alert(`Data untuk ${formData.name} berhasil disimpan!`);
            setFormData({ name: '', age: '' });

        } catch (error) {
            console.error("Gagal menyimpan data:", error);
            alert("Terjadi kesalahan saat menyimpan data. Cek konsol untuk detail.");
        }
    };

    return (
        <div>
            <h2>Tulis Data ke Firebase (Create)</h2>
            <form onSubmit={handleSubmit}>
                <div>
                    <label htmlFor="name">Nama:</label>
                    <input
                        type="text"
                        id="name"
                        name="name"
                        value={formData.name}
                        onChange={handleChange}
                        required
                    />
                </div>
                <div>
                    <label htmlFor="age">Usia:</label>
                    <input
                        type="number"
                        id="age"
                        name="age"
                        value={formData.age}
                        onChange={handleChange}
                        required
                    />
                </div>
                <button type="submit" style={{ backgroundColor: '#28a745' }}>Tambah Data</button>
            </form>
        </div>
    );
};

export default Write;
