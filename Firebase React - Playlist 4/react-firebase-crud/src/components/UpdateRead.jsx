import React, { useState } from 'react';
import { db } from '../firebaseConfig';
// Import fungsi 'remove' untuk operasi DELETE
import { ref, get, remove } from 'firebase/database'; 
import { useNavigate } from 'react-router-dom';

const UpdateRead = () => {
    const navigate = useNavigate();
    const [records, setRecords] = useState([]);

    // Fungsi FetchData yang sama (digunakan juga untuk refresh setelah delete)
    const fetchData = async () => {
        try {
            const dataRef = ref(db, 'data/records');
            const snapshot = await get(dataRef);

            if (snapshot.exists()) {
                const dataObject = snapshot.val() || {};
                const recordKeys = Object.keys(dataObject);
                
                const dataWithIDs = recordKeys.map(key => {
                    return {
                        id: key, 
                        ...dataObject[key] 
                    };
                });
                
                setRecords(dataWithIDs);
            } else {
                console.log("Tidak ada data ditemukan di path.");
                setRecords([]);
            }
        } catch (error) {
            console.error("Gagal mengambil data:", error);
            alert("Terjadi kesalahan saat membaca data. Cek konsol.");
        }
    };
    
    // --- FUNGSI BARU: DELETE DATA ---
    const handleDelete = async (recordId) => {
        // Konfirmasi sebelum menghapus
        if (!window.confirm(`Anda yakin ingin menghapus record dengan ID: ${recordId}?`)) {
            return;
        }

        try {
            // Tentukan referensi ke record spesifik
            const recordRef = ref(db, `data/records/${recordId}`);
            
            // Eksekusi penghapusan
            await remove(recordRef);

            alert(`Record dengan ID ${recordId} berhasil dihapus.`);

            // Refresh daftar data
            fetchData(); 

        } catch (error) {
            console.error("Gagal menghapus data:", error);
            alert("Terjadi kesalahan saat menghapus data. Cek konsol.");
        }
    };
    // ------------------------------------

    return (
        <div>
            <h2>Baca Data untuk Update/Delete (Termasuk ID Unik)</h2>
            <div style={{ textAlign: 'center', marginBottom: '25px' }}>
                <button onClick={fetchData} style={{ backgroundColor: '#6c757d' }}>Fetch Data dengan ID</button>
            </div>

            {/* Menampilkan Data */}
            {records.length > 0 ? (
                <ul>
                    {records.map((item) => (
                        <li 
                            key={item.id} 
                            style={{ border: '1px solid #ccc', padding: '10px', margin: '5px auto', width: '80%', textAlign: 'left' }}
                        >
                            <strong>ID:</strong> {item.id}
                            <br />
                            <strong>Nama:</strong> {item.name}, 
                            <strong> Usia:</strong> {item.age}
                            <div style={{ marginTop: '10px' }}>
                                {/* Tombol UPDATE yang mengarahkan ke rute dinamis */}
                                <button 
                                    onClick={() => navigate(`/update/${item.id}`)}
                                    style={{ marginRight: '10px', backgroundColor: '#ffc107' }}
                                >
                                    Update
                                </button>
                                {/* Tombol DELETE diintegrasikan */}
                                <button 
                                    onClick={() => handleDelete(item.id)}
                                    style={{ backgroundColor: '#dc3545' }}
                                >
                                    Delete
                                </button>
                            </div>
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Klik 'Fetch Data dengan ID' untuk memuat record.</p>
            )}
        </div>
    );
};

export default UpdateRead;
