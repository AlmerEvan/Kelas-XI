import React, { useState, useEffect } from 'react';
import { db } from '../firebaseConfig';
import { ref, set, get } from 'firebase/database';
import { useNavigate, useParams } from 'react-router-dom'; 

const UpdateWrite = () => {
    const navigate = useNavigate();
    // Ambil ID dari URL
    const { id } = useParams(); 

    const [formData, setFormData] = useState({
        name: '',
        age: ''
    });

    // --- EFFECT: Mengambil data lama saat komponen dimuat ---
    useEffect(() => {
        const fetchOldData = async () => {
            if (!id) return; // Pastikan ID ada

            try {
                // Buat referensi ke record spesifik
                const recordRef = ref(db, `data/records/${id}`);
                const snapshot = await get(recordRef);

                if (snapshot.exists()) {
                    const oldData = snapshot.val();
                    // Set state dengan data lama
                    setFormData({
                        name: oldData.name,
                        age: oldData.age
                    });
                } else {
                    alert("Record tidak ditemukan.");
                    navigate('/update'); // Kembali ke daftar jika tidak ditemukan
                }
            } catch (error) {
                console.error("Gagal memuat data lama:", error);
                alert("Gagal memuat data lama. Cek konsol.");
            }
        };

        fetchOldData();
    }, [id, navigate]); // Rerun saat ID atau navigate berubah

    const handleChange = (e) => {
        const { name, value } = e.target;
        setFormData(prevState => ({
            ...prevState,
            [name]: value
        }));
    };

    // --- HANDLER: Memperbarui data ke Firebase ---
    const handleUpdate = async (e) => {
        e.preventDefault();
        
        try {
            // Tentukan referensi ke record spesifik menggunakan ID
            const recordRef = ref(db, `data/records/${id}`);
            
            // Gunakan set() untuk menimpa/memperbarui data
            await set(recordRef, {
                name: formData.name,
                age: formData.age,
                timestamp: Date.now() 
            });

            alert(`Data dengan ID ${id} berhasil diperbarui!`);
            navigate('/update'); // Kembali ke daftar setelah update

        } catch (error) {
            console.error("Gagal memperbarui data:", error);
            alert("Terjadi kesalahan saat memperbarui data. Cek konsol.");
        }
    };

    return (
        <div>
            <h2>Perbarui Data (ID: {id})</h2>
            <form onSubmit={handleUpdate}>
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
                <button type="submit" style={{ backgroundColor: '#ffc107' }}>Update Data</button>
            </form>
        </div>
    );
};

export default UpdateWrite;
