import React, { useState } from 'react';
import { db } from '../firebaseConfig';
import { ref, get } from 'firebase/database';
import { useNavigate } from 'react-router-dom'; // Import useNavigate

const Read = () => {
    const navigate = useNavigate(); // Inisialisasi hook
    const [records, setRecords] = useState([]);

    const fetchData = async () => {
        try {
            const dataRef = ref(db, 'data/records');
            const snapshot = await get(dataRef);

            if (snapshot.exists()) {
                const dataObject = snapshot.val() || {};
                const dataArray = Object.values(dataObject);
                
                setRecords(dataArray);
                alert(`Berhasil mengambil ${dataArray.length} record data.`);
            } else {
                console.log("Tidak ada data ditemukan di path.");
                setRecords([]);
                alert("Tidak ada record data ditemukan.");
            }
        } catch (error) {
            console.error("Gagal mengambil data:", error);
            alert("Terjadi kesalahan saat membaca data. Cek konsol.");
        }
    };

    return (
        <div>
            <h2>Baca Data dari Firebase (Read)</h2>
            <div style={{ textAlign: 'center', marginBottom: '25px' }}>
                <button onClick={fetchData} style={{ backgroundColor: '#6c757d' }}>Fetch Data</button>
            </div>

            {/* Menampilkan Data */}
            {records.length > 0 ? (
                <ul>
                    {records.map((item, index) => (
                        <li key={index} style={{ border: '1px solid #ccc', padding: '10px', margin: '5px 0' }}>
                            <strong>Nama:</strong> {item.name}, 
                            <strong> Usia:</strong> {item.age}
                        </li>
                    ))}
                </ul>
            ) : (
                <p>Klik 'Fetch Data' untuk memuat record.</p>
            )}
        </div>
    );
};

export default Read;
